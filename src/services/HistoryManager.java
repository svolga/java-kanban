package services;

import model.Task;

import java.util.List;

public interface HistoryManager<T extends Task> {
    T addTask(T task);

    List<T> getHistory();
}
