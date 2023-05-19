package services;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    Task createTask(Task task);
    void updateTask(Task task);
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
    Subtask createSubtask(Subtask subtask);
    void updateSubtask(Subtask subtask);
    List<Subtask> getAllSubtasks();
    List<Subtask> getAllSubtasksByEpicId(int epicId);
    Subtask getSubtask(int id);
    void removeSubtask(int id);
    List<Task> getHistory();
}
