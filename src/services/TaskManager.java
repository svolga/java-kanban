package services;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    Task createTask(Task task);
    void updateTask(Task task);
    Task getTaskById(int id);
    List<Task> getAllTasks();
    void clearTasks();
    void removeTask(int id);
    void printTasks();
    void removeAllSubtasks();
    Epic createEpic(Epic epic);
    void updateEpic(Epic epic);
    Epic getEpicById(int id);
    List<Epic> getAllEpics();
    void clearEpics();
    void printEpics();
    void removeEpic(int epicId);
    Subtask createSubtask(Subtask subtask);
    void updateSubtask(Subtask subtask);
    List<Subtask> getAllSubtasks();
    List<Subtask> getAllSubtasksByEpicId(int epicId);
    void clearSubtasks();
    void removeSubtask(int id);
    void printSubtasks();
}
