package services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Epic;
import model.ItemStatus;
import model.Subtask;
import repository.Repo;

public class SubtaskManager extends Manager implements Repo<Subtask> {

    EpicManager epicManager;
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    public SubtaskManager(EpicManager epicManager) {
        this.epicManager = epicManager;
    }

    @Override
    public Subtask create(Subtask subtask) {
        subtask.setId(getNextId());
        subtasks.put(subtask.getId(), subtask);

        epicManager.addSubtask(subtask);
        updateEpicStatus(subtask.getEpicId());
        return subtask;
    }

    @Override
    public void update(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public Subtask getById(int id) {
        return subtasks.get(id);
    }

    @Override
    public Map<Integer, Subtask> getAll() {
        return subtasks;
    }

    @Override
    public void clear() {
        subtasks.forEach((key, subtask) -> {
            Epic epic = epicManager.getById(subtask.getEpicId());
            epicManager.removeSubtask(subtask);
            updateEpicStatus(epic.getId());
        });

        subtasks.clear();
    }

    @Override
    public void delete(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            epicManager.removeSubtask(subtask);
            updateEpicStatus(subtask.getEpicId());
            subtasks.remove(subtask.getId());
        }
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epicManager.getById(epicId);
        if (null != epic) {
            List<Integer> subtaskIds = epic.getSubtaskIds();
            int size = subtaskIds.size();
            if (size == 0) {
                epic.setStatus(ItemStatus.NEW);
            } else {
                ItemStatus status = ItemStatus.DONE;
                for (Integer subTaskId : subtaskIds) {
                    Subtask subtask = subtasks.get(subTaskId);
                    if (subtask.getStatus() != ItemStatus.DONE) {
                        status = ItemStatus.IN_PROGRESS;
                        break;
                    }
                }
                epic.setStatus(status);
            }
        }
    }

    public void print() {
        subtasks.forEach((key, subtask) -> System.out.println(subtask));
    }

}
