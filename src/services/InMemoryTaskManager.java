package services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Comparator;

import exception.IntersectionDateIntervalException;
import model.Epic;
import model.enums.ItemStatus;
import model.Subtask;
import model.Task;
import util.Const;
import util.Managers;
import validate.IntersectionDateIntervalValidator;

public class InMemoryTaskManager implements TaskManager {

    protected int nextId = 0;

    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
        Comparator.nullsLast(Comparator.naturalOrder())
        ));

    protected final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Const.DATE_TIME_FORMAT);
    private final IntersectionDateIntervalValidator<Task> intersectionDateIntervalValidator = new IntersectionDateIntervalValidator<>();

    @Override
    public Task createTask(Task task) throws IntersectionDateIntervalException {
        intersectionDateIntervalValidator.validate(getPrioritizedTasks(), task);
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void updateTask(Task task) throws IntersectionDateIntervalException {
        if (tasks.containsKey(task.getId())) {
            intersectionDateIntervalValidator.validate(getPrioritizedTasks(), task);
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void clearSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            updateEpicInternal(epic.getId());
        }
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic originalEpic = epics.get(epic.getId());
        if (originalEpic != null) {
            originalEpic.setDescription(epic.getDescription());
            originalEpic.setTitle(epic.getTitle());
        }
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearEpics() {
        epics.keySet().forEach(historyManager::remove);
        epics.clear();
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();
    }

    @Override
    public void removeEpic(int epicId) {
        Epic epic = epics.remove(epicId);
        if (epic != null) {
            for (int id : epic.getSubtaskIds()) {
                subtasks.remove(id);
                historyManager.remove(id);
            }
            historyManager.remove(epicId);
        }
    }

    @Override
    public Subtask createSubtask(Subtask subtask) throws IntersectionDateIntervalException {
        Epic epic = epics.get(subtask.getEpicId());
        if (null != epic) {
            intersectionDateIntervalValidator.validate(getPrioritizedTasks(), subtask);
            subtask.setId(getNextId());
            subtasks.put(subtask.getId(), subtask);

            epic.addSubtask(subtask.getId());
            updateEpicInternal(subtask.getEpicId());
        }
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) throws IntersectionDateIntervalException {
        if (subtasks.containsKey(subtask.getId())) {
            intersectionDateIntervalValidator.validate(getPrioritizedTasks(), subtask);
            subtasks.put(subtask.getId(), subtask);
            updateEpicInternal(subtask.getEpicId());
        }
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return List.of();
        }
        ArrayList<Subtask> tasks = new ArrayList<>();
        for (int id : epic.getSubtaskIds()) {
            tasks.add(subtasks.get(id));
        }
        return tasks;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (null != subtask) {
            Epic epic = epics.get(subtask.getEpicId());
            if (null != epic) {
                epic.removeSubtask(subtask.getId());
                updateEpicInternal(epic.getId());
            }
            historyManager.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        prioritizedTasks.clear();
        prioritizedTasks.addAll(getAllTasks());
        prioritizedTasks.addAll(getAllSubtasks());
        return prioritizedTasks;
    }

    private int getNextId() {
        return ++nextId;
    }

    private void updateEpicStatus(Epic epic) {
        if (null != epic) {
            epic.setStatus(calculateEpicStatus(epic));
        }
    }

    private void updateEpicTime(Epic epic) {
        if (null != epic) {
            List<Subtask> subtasks = getAllSubtasksByEpicId(epic.getId());

            LocalDateTime minStartTime = null;
            LocalDateTime maxEndTime = null;
            long totalDuration = 0;

            for (Subtask subtask : subtasks) {

                if (minStartTime == null) {
                    minStartTime = subtask.getStartTime();
                }
                else {
                    if (subtask.getStartTime() != null && subtask.getStartTime().isBefore(minStartTime)){
                        minStartTime = subtask.getStartTime();
                    }
                }

                if (maxEndTime == null) {
                    maxEndTime = subtask.getEndTime();
                }
                else {
                    if (subtask.getEndTime() != null && subtask.getEndTime().isAfter(maxEndTime)){
                        maxEndTime = subtask.getEndTime();
                    }
                }

                totalDuration += subtask.getDuration();
            }

            epic.setStartTime(minStartTime);
            epic.setEndTime(maxEndTime);
            epic.setDuration(totalDuration);
        }
    }

    private void updateEpicInternal(int epicId) {
        Epic epic = epics.get(epicId);
        if (null != epic) {
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
    }

    private ItemStatus calculateEpicStatus(Epic epic) {
        ItemStatus defaultStatus = ItemStatus.IN_PROGRESS;
        List<Integer> subtaskIds = epic.getSubtaskIds();
        final int size = epic.getSubtaskIds().size();

        if (size == 0)
            return ItemStatus.NEW;
        else {
            int iDoneCount = 0;
            int iNewCount = 0;

            for (int subTaskId : subtaskIds) {
                Subtask subtask = subtasks.get(subTaskId);

                if (subtask.getStatus().equals(ItemStatus.NEW)) {
                    ++iNewCount;
                } else if (subtask.getStatus().equals(ItemStatus.DONE)) {
                    ++iDoneCount;
                }

                if (iNewCount > 0 && iDoneCount > 0)
                    return defaultStatus;
            }

            if (size == iNewCount)
                return ItemStatus.NEW;
            else if (size == iDoneCount)
                return ItemStatus.DONE;
        }

        return defaultStatus;
    }

}
