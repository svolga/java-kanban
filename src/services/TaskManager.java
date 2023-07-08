package services;

import exception.IntersectionDateIntervalException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    Task createTask(Task task) throws IntersectionDateIntervalException;
    void updateTask(Task task) throws IntersectionDateIntervalException;
    Task getTask(int id);
    List<Task> getAllTasks();
    void clearTasks();
    Task removeTask(int id);
    void clearSubtasks();
    Epic createEpic(Epic epic);
    void updateEpic(Epic epic);
    Epic getEpic(int id);
    List<Epic> getAllEpics();
    void clearEpics();
    Epic removeEpic(int epicId);
    Subtask createSubtask(Subtask subtask) throws IntersectionDateIntervalException;
    void updateSubtask(Subtask subtask) throws IntersectionDateIntervalException;
    List<Subtask> getAllSubtasks();
    List<Subtask> getAllSubtasksByEpicId(int epicId);
    Subtask getSubtask(int id);
    Subtask removeSubtask(int id);
    List<Task> getHistory();
    List<Task> getPrioritizedTasks();
}
