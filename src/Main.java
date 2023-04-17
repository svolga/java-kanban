import model.Epic;
import model.ItemStatus;
import model.Subtask;
import model.Task;
import services.Manager;

import java.util.List;

public class Main {

    private final Manager manager = new Manager();

    public static void main(String[] args) {
        new Main().run2();
    }

    private void run2() {
        System.out.println("\nЗадачи (tasks)");
        createDemoTasks();
        manager.printTasks();

        System.out.println("\nДобавили подзадачи (subtasks)");
        createDemoEpics();
        manager.printSubtasks();
        System.out.println("Эпики (epics)");
        manager.printEpics();

        System.out.println("\nИзменились статусы всех подзадач на DONE");
        changeSubtasksStatus();
        System.out.println("Подзадачи (subtasks) после смены статусов");
        manager.printSubtasks();
        System.out.println("Эпики (epics) после смены статусов");
        manager.printEpics();

        System.out.println("\nУдаление подзадач");
        manager.clearSubtasks();
        System.out.println("Подзадачи (subtasks) после удаления");
        manager.printSubtasks();
        System.out.println("Эпики (epics) после удаления подзадач");
        manager.printEpics();

        System.out.println("\nДобавили новую порцию подзадач");
        addDemoSubtasks();
        System.out.println("Подзадачи (subtasks) после нового добавления");
        manager.printSubtasks();
        System.out.println("Эпики (epics) после добавления подзадач");
        manager.printEpics();

        System.out.println("\nУдаляем все Эпики");
        System.out.println("*** Остались эпики после удаления ***");
        manager.clearEpics();
        manager.printEpics();

        System.out.println("### Subtasks после удаления Эпиков ###");
        manager.printSubtasks();
    }

    private void changeSubtasksStatus() {
        List<Subtask> subtasks = manager.getAllSubtasks();
        for (Subtask subtask : subtasks) {
            subtask.setStatus(ItemStatus.DONE);
            manager.updateSubtask(subtask);
        }
    }

    private void createDemoEpics() {
        Epic epic1 = manager.createEpic(new Epic(0, "Первый Epic", "Описание первого эпика"));
        Epic epic2 = manager.createEpic(new Epic(0, "Второй Epic", "Описание второго эпика"));

        manager.createSubtask(new Subtask(0, "Первая подзадача", "Описание первой подзадачи", epic1.getId()));
        manager.createSubtask(new Subtask(0, "Вторая подзадача", "Описание второй подзадачи", epic1.getId()));
        manager.createSubtask(new Subtask(0, "Третья подзадача", "Описание третьей подзадачи", epic2.getId()));
    }

    private void createDemoTasks() {
        manager.createTask(new Task(0, "Первая задача", "Описание первой задачи"));
        manager.createTask(new Task(0, "Вторая задача", "Описание второй задачи"));
    }

    private void addDemoSubtasks() {
        List<Epic> epics = manager.getAllEpics();
        int i = 1;
        for (Epic epic : epics) {
            manager.createSubtask(new Subtask(0, i++ + " подзадача (Вторая партия)", "Описание подзадачи", epic.getId()));
        }
    }

}