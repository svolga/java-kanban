package services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Epic;
import model.Subtask;
import repository.Repo;

public class EpicManager extends Manager implements Repo<Epic> {

    private Map<Integer, Epic> epics = new HashMap<>();

    public void addSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        List<Integer> subTasks = epic.getSubtaskIds();
        subTasks.add(subtask.getId());
    }

    public void removeSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        List<Integer> subTasks = epic.getSubtaskIds();
        subTasks.remove(subTasks.indexOf(subtask.getId()));
    }

    @Override
    public Epic create(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void update(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public Epic getById(int id) {
        return epics.get(id);
    }

    @Override
    public Map<Integer, Epic> getAll() {
        return epics;
    }

    @Override
    public void clear() {
        epics.clear();
    }

    @Override
    public void delete(int id) {
        epics.remove(id);
    }

    public void print() {
        epics.forEach((key, epic) -> System.out.println(epic));
    }

}
