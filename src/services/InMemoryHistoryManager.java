package services;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    List<Task> tasks = new ArrayList<>();
    private final int LIMIT = 10;

    @Override
    public void addTask(Task task) {
        tasks.add(task);
        clearLimit();
    }

    @Override
    public List getHistory() {
        return tasks;
    }

    private void clearLimit() {
        int size = tasks.size();
        if (size > LIMIT) {
            tasks.subList(0, size - LIMIT).clear();
        }
    }

}
