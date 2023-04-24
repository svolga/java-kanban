package services;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.Task;

public class InMemoryHistoryManager implements HistoryManager {

    private final int LIMIT = 10;
    private List<Task> tasks = new LinkedList<>();

    @Override
    public void addTask(Task task) {
        if (task != null) {
            clearLimit();
            tasks.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(tasks);
    }

    private void clearLimit() {
        if (LIMIT == tasks.size()) {
            tasks.remove(0);
        }
    }

}
