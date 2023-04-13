package services;

import model.Task;
import repository.Repo;

import java.util.HashMap;
import java.util.Map;

public class TaskManager extends Manager implements Repo<Task> {

    private Map<Integer, Task> tasks = new HashMap<>();

    @Override
    public Task create(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void update(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public Task getById(int id) {
        return tasks.get(id);
    }

    @Override
    public Map<Integer, Task> getAll() {
        return tasks;
    }

    @Override
    public void clear() {
        tasks.clear();
    }

    @Override
    public void delete(int id) {
        tasks.remove(id);
    }

    public void print() {
        tasks.forEach((key, task) -> System.out.println(task));
    }

}
