import model.Epic;
import model.ItemStatus;
import model.Subtask;
import model.Task;
import services.InMemoryTaskManager;

import java.util.List;

public class Main {

    private final InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        System.out.println("\nЗадачи (tasks)");
        createDemoTasks();
        inMemoryTaskManager.printTasks();

        System.out.println("\nДобавили подзадачи (subtasks)");
        createDemoEpics();
        inMemoryTaskManager.printSubtasks();
        System.out.println("Эпики (epics)");
        inMemoryTaskManager.printEpics();

        System.out.println("\nИзменились статусы всех подзадач на DONE");
        changeSubtasksStatus();
        System.out.println("Подзадачи (subtasks) после смены статусов");
        inMemoryTaskManager.printSubtasks();
        System.out.println("Эпики (epics) после смены статусов");
        inMemoryTaskManager.printEpics();

        System.out.println("\nУдаление подзадач");
        inMemoryTaskManager.clearSubtasks();
        System.out.println("Подзадачи (subtasks) после удаления");
        inMemoryTaskManager.printSubtasks();
        System.out.println("Эпики (epics) после удаления подзадач");
        inMemoryTaskManager.printEpics();

        System.out.println("\nДобавили новую порцию подзадач");
        addDemoSubtasks();
        System.out.println("Подзадачи (subtasks) после нового добавления");
        inMemoryTaskManager.printSubtasks();
        System.out.println("Эпики (epics) после добавления подзадач");
        inMemoryTaskManager.printEpics();

        System.out.println("\nУдаляем все Эпики");
        System.out.println("*** Остались эпики после удаления ***");
        inMemoryTaskManager.clearEpics();
        inMemoryTaskManager.printEpics();

        System.out.println("### Subtasks после удаления Эпиков ###");
        inMemoryTaskManager.printSubtasks();
    }

    private void changeSubtasksStatus() {
        List<Subtask> subtasks = inMemoryTaskManager.getAllSubtasks();
        for (Subtask subtask : subtasks) {
            subtask.setStatus(ItemStatus.DONE);
            inMemoryTaskManager.updateSubtask(subtask);
        }
    }

    private void createDemoEpics() {
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic(0, "Первый Epic", "Описание первого эпика"));
        Epic epic2 = inMemoryTaskManager.createEpic(new Epic(0, "Второй Epic", "Описание второго эпика"));

        inMemoryTaskManager.createSubtask(new Subtask(0, "Первая подзадача", "Описание первой подзадачи", epic1.getId()));
        inMemoryTaskManager.createSubtask(new Subtask(0, "Вторая подзадача", "Описание второй подзадачи", epic1.getId()));
        inMemoryTaskManager.createSubtask(new Subtask(0, "Третья подзадача", "Описание третьей подзадачи", epic2.getId()));
    }

    private void createDemoTasks() {
        inMemoryTaskManager.createTask(new Task(0, "Первая задача", "Описание первой задачи"));
        inMemoryTaskManager.createTask(new Task(0, "Вторая задача", "Описание второй задачи"));
    }

    private void addDemoSubtasks() {
        List<Epic> epics = inMemoryTaskManager.getAllEpics();
        int i = 1;
        for (Epic epic : epics) {
            inMemoryTaskManager.createSubtask(new Subtask(0, i++ + " подзадача (Вторая партия)", "Описание подзадачи", epic.getId()));
        }
    }

}