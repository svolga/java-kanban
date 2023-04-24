package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Epic;
import model.ItemStatus;
import model.Subtask;
import model.Task;

public class InMemoryTaskManager implements TaskManager{

    private int nextId = 1;

    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Task> tasks = new HashMap<>();

    private int getNextId() {
        return nextId++;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void printTasks() {
        tasks.forEach((key, task) -> System.out.println(task));
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            updateEpicStatus(epic.getId());
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
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void printEpics() {
        epics.forEach((key, epic) -> System.out.println(epic));
    }

    @Override
    public void removeEpic(int epicId) {
        Epic epic = epics.remove(epicId);
        if (epic != null) {
            for (int id : epic.getSubtaskIds()) {
                subtasks.remove(id);
            }
        }
    }

    @Override
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

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> tasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        for (int id : epic.getSubtaskIds()) {
            tasks.add(subtasks.get(id));
        }
        return tasks;
    }

    @Override
    public void clearSubtasks() {
        epics.forEach((key, epic) -> {
            epic.removeAllSubtasks();
            updateEpicStatus(epic.getId());
        });
        subtasks.clear();
    }

    @Override
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

    @Override
    public void printSubtasks() {
        subtasks.forEach((key, subtask) -> System.out.println(subtask));
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

}
