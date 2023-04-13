import model.Epic;
import model.ItemStatus;
import model.Subtask;
import model.Task;
import services.EpicManager;
import services.SubtaskManager;
import services.TaskManager;

import java.util.Map;

public class Main {

    TaskManager taskManager = new TaskManager();
    EpicManager epicManager = new EpicManager();
    SubtaskManager subtaskManager = new SubtaskManager(epicManager);

    public static void main(String[] args) {
        new Main().run();
    }

    private void run(){

        System.out.println("Задачи (tasks)");
        createDemoTasks();
        taskManager.print();

        System.out.println("Подзадачи (subtasks)");
        createDemoEpics();
        subtaskManager.print();
        System.out.println("Эпики (epics)");
        epicManager.print();

        System.out.println("Изменились статусы всех подзадач на DONE");
        changeSubtasksStatus();
        System.out.println("Подзадачи (subtasks) после смены статусов");
        subtaskManager.print();
        System.out.println("Эпики (epics) после смены статусов");
        epicManager.print();

        System.out.println("Удаление подзадач");
        subtaskManager.clear();
        System.out.println("Подзадачи (subtasks) после удаления");
        subtaskManager.print();
        System.out.println("Эпики (epics) после удаления подзадач");
        epicManager.print();
    }

    private void changeSubtasksStatus(){
        Map<Integer, Subtask> subtasks = subtaskManager.getAll();
        for (Integer key : subtasks.keySet()) {
            Subtask subtask = subtasks.get(key);
            subtask.setStatus(ItemStatus.DONE);
            subtaskManager.update(subtask);
        }
    }

    private void createDemoEpics() {
        Epic epic1 = epicManager.create(new Epic(0, "Первый Epic", "Описание первого эпика"));
        Epic epic2 = epicManager.create(new Epic(0, "Второй Epic", "Описание второго эпика"));

        Subtask subtask11 = subtaskManager.create(new Subtask(0, "Первая подзадача", "Описание первой подзадачи", epic1.getId()));
        Subtask subtask12 = subtaskManager.create(new Subtask(0, "Вторая подзадача", "Описание второй подзадачи", epic1.getId()));
        Subtask subtask21 = subtaskManager.create(new Subtask(0, "Третья подзадача", "Описание третьей подзадачи", epic2.getId()));
    }

    private void createDemoTasks() {
        taskManager.create(new Task(0, "Первая задача", "Описание первой задачи"));
        taskManager.create(new Task(0, "Вторая задача", "Описание второй задачи"));
    }

}