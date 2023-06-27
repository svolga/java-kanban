package services;

import exception.IntersectionDateIntervalException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {
    Task createTask(Task task) throws IntersectionDateIntervalException;
    void updateTask(Task task) throws IntersectionDateIntervalException;
    Task getTask(int id);
    List<Task> getAllTasks();
    void clearTasks();
    void removeTask(int id);
    void clearSubtasks();
    Epic createEpic(Epic epic);
    void updateEpic(Epic epic);
    Epic getEpic(int id);
    List<Epic> getAllEpics();
    void clearEpics();
    void removeEpic(int epicId);
    Subtask createSubtask(Subtask subtask) throws IntersectionDateIntervalException;
    void updateSubtask(Subtask subtask) throws IntersectionDateIntervalException;
    List<Subtask> getAllSubtasks();
    List<Subtask> getAllSubtasksByEpicId(int epicId);
    Subtask getSubtask(int id);
    void removeSubtask(int id);
    List<Task> getHistory();
    List<Task> getPrioritizedTasks();
}
