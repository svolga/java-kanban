package services;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.stream.Collectors;

import event.ListEvent;
import model.Epic;
import model.ItemStatus;
import model.Subtask;
import repository.Repo;

public class SubtaskManager extends Manager implements Repo<Subtask>, PropertyChangeListener {

    private final EpicManager epicManager;
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    public SubtaskManager(EpicManager epicManager) {
        this.epicManager = epicManager;
        epicManager.addPropertyChangeListener(this);
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
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    @Override
    public Subtask getById(int id) {
        return subtasks.get(id);
    }

    @Override
    public List<Subtask> getAll() {
        return new ArrayList<>(subtasks.values());

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
            if (subtaskIds.size() == 0 || isTheSameStatus(subtaskIds, ItemStatus.NEW)) {
                epic.setStatus(ItemStatus.NEW);
            } else if (isTheSameStatus(subtaskIds, ItemStatus.DONE)) {
                epic.setStatus(ItemStatus.DONE);
            } else {
                epic.setStatus(ItemStatus.IN_PROGRESS);
            }
        }
    }

    private boolean isTheSameStatus(List<Integer> subtaskIds, ItemStatus checkStatus) {
        boolean isTheSame = false;
        for (Integer subTaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subTaskId);
            if (subtask.getStatus() == checkStatus) {
                isTheSame = true;
            } else {
                isTheSame = false;
                break;
            }
        }
        return isTheSame;
    }

    public void print() {
        subtasks.forEach((key, subtask) -> System.out.println(subtask));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (propertyName.equals(ListEvent.DELETED_EPIC)) {
            int epicId = (int) evt.getNewValue();
            System.out.println("Оповещение для свойства: " + propertyName + " удалили epic c id = " + epicId);
            Set<Integer> ids = findByEpicId(epicId);
            if (null != ids) {
                ids.forEach(id -> subtasks.remove(id));
            }
        }
    }

    private Set<Integer> findByEpicId(int iEpicId) {
        return subtasks.values().stream()
                .filter(subtask -> subtask.getEpicId() == iEpicId)
                .map(Subtask::getId)
                .collect(Collectors.toSet());

    }

}
