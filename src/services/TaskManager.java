package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Task;
import repository.Repo;

public class TaskManager extends Manager implements Repo<Task> {

    private final Map<Integer, Task> tasks = new HashMap<>();

    @Override
    public Task create(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void update(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public Task getById(int id) {
        return tasks.get(id);
    }

    @Override
    public List<Task> getAll() {
        return new ArrayList<>(tasks.values());
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
