package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.Epic;
import model.ItemStatus;
import model.Subtask;
import model.Task;

public class Manager {

    private int nextId = 1;

    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Task> tasks = new HashMap<>();

    private int getNextId() {
        return nextId++;
    }

    public Task createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        return task;
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void printTasks() {
        tasks.forEach((key, task) -> System.out.println(task));
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            updateEpicStatus(epic.getId());
        }
    }

    public Epic createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public void updateEpic(Epic epic) {
        Epic originalEpic = epics.get(epic.getId());
        if (originalEpic != null) {
            originalEpic.setDescription(epic.getDescription());
            originalEpic.setTitle(epic.getTitle());
        }
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void printEpics() {
        epics.forEach((key, epic) -> System.out.println(epic));
    }

    public void removeEpic(int epicId) {
        Epic epic = epics.remove(epicId);
        if (epic != null) {
            for (int id : epic.getSubtaskIds()) {
                subtasks.remove(id);
            }
            updateEpicStatus(epicId);
        }
    }

    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(getNextId());
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (null != epic) {
            epic.addSubtask(subtask.getId());
        }

        updateEpicStatus(subtask.getEpicId());
        return subtask;
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Subtask> getAllSubtasksByEpicId(int epicId) {
        return subtasks
                .values()
                .stream()
                .filter(subtask -> subtask.getEpicId() == epicId)
                .collect(Collectors.toList());
    }

    public void clearSubtasks() {
        epics.forEach((key, epic) -> {
            epic.removeAllSubtasks();
            updateEpicStatus(epic.getId());
        });
        subtasks.clear();
    }

    public void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (null != subtask) {
            Epic epic = epics.get(subtask.getEpicId());
            if (null != epic) {
                epic.removeSubtask(subtask.getId());
                updateEpicStatus(epic.getId());
            }
        }
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = getEpicById(epicId);
        if (null != epic) {
            epic.setStatus(calculateEpicStatus(epic));
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

    public void printSubtasks() {
        subtasks.forEach((key, subtask) -> System.out.println(subtask));
    }

}
