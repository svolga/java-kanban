package services;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

import event.ListEvent;
import model.Epic;
import model.Subtask;
import repository.Repo;

public class EpicManager extends Manager implements Repo<Epic> {

    private final Map<Integer, Epic> epics = new HashMap<>();
    private final PropertyChangeSupport support;

    public EpicManager() {
        support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void addSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (null != epic) {
            epic.addSubtask(subtask.getId());
        }
    }

    public void removeSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (null != epic) {
            epic.removeSubtask(subtask.getId());
        }
    }

    public void removeAllSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (null != epic) {
            epic.removeAllSubtasks();
        }
    }

    @Override
    public Epic create(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void update(Epic epic) {
        int epicId = epic.getId();
        if (epics.containsKey(epicId)) {
            epics.put(epicId, epic);
        }
    }

    @Override
    public Epic getById(int id) {
        return epics.get(id);
    }

    @Override
    public List<Epic> getAll() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clear() {
        Iterator<Map.Entry<Integer, Epic>> iter = epics.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, Epic> entry = iter.next();
            support.firePropertyChange(ListEvent.DELETED_EPIC, 0, entry.getValue().getId());
            iter.remove();
        }
    }

    public void print() {
        epics.forEach((key, epic) -> System.out.println(epic));
    }

    @Override
    public void delete(int id) {
        support.firePropertyChange(ListEvent.DELETED_EPIC, 0, id);
        epics.remove(id);
    }

}
