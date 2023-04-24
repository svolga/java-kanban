import model.Epic;
import model.ItemStatus;
import model.Subtask;
import model.Task;
import services.TaskManager;
import util.Managers;

import java.util.List;

public class Main {

    private final TaskManager inMemoryTaskManager = Managers.getDefault();

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        System.out.println("\nЗадачи (tasks)");
        createDemoTasks();
        inMemoryTaskManager.printTasks();
        inMemoryTaskManager.printHistory();

        System.out.println("\nДобавили подзадачи (subtasks)");
        createDemoEpics();
        inMemoryTaskManager.printSubtasks();
        System.out.println("Эпики (epics)");
        inMemoryTaskManager.printEpics();
        inMemoryTaskManager.printHistory();

        System.out.println("\nИзменились статусы всех подзадач на DONE");
        changeSubtasksStatus();
        System.out.println("Подзадачи (subtasks) после смены статусов");
        inMemoryTaskManager.printSubtasks();
        System.out.println("Эпики (epics) после смены статусов");
        inMemoryTaskManager.printEpics();
        inMemoryTaskManager.printHistory();

        System.out.println("\nУдаление подзадач");
        inMemoryTaskManager.clearSubtasks();
        System.out.println("Подзадачи (subtasks) после удаления");
        inMemoryTaskManager.printSubtasks();
        System.out.println("Эпики (epics) после удаления подзадач");
        inMemoryTaskManager.printEpics();
        inMemoryTaskManager.printHistory();

        System.out.println("\nДобавили новую порцию подзадач");
        addDemoSubtasks();
        System.out.println("Подзадачи (subtasks) после нового добавления");
        inMemoryTaskManager.printSubtasks();
        System.out.println("Эпики (epics) после добавления подзадач");
        inMemoryTaskManager.printEpics();
        inMemoryTaskManager.printHistory();

        System.out.println("\nУдаляем все Эпики");
        System.out.println("*** Остались эпики после удаления ***");
        inMemoryTaskManager.clearEpics();
        inMemoryTaskManager.printEpics();
        inMemoryTaskManager.printHistory();

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
        Epic epic3 = inMemoryTaskManager.createEpic(new Epic(0, "Третий Epic", "Описание третьего эпика"));

        inMemoryTaskManager.createSubtask(new Subtask(0, "Первая подзадача", "Описание первой подзадачи", epic1.getId()));
        inMemoryTaskManager.createSubtask(new Subtask(0, "Вторая подзадача", "Описание второй подзадачи", epic2.getId()));
        inMemoryTaskManager.createSubtask(new Subtask(0, "Третья подзадача", "Описание третьей подзадачи", epic3.getId()));
    }

    private void createDemoTasks() {
        inMemoryTaskManager.createTask(new Task(0, "Первая задача", "Описание первой задачи"));
        inMemoryTaskManager.createTask(new Task(0, "Вторая задача", "Описание второй задачи"));
        inMemoryTaskManager.createTask(new Task(0, "Третья задача", "Описание третьей задачи"));
    }

    private void addDemoSubtasks() {
        List<Epic> epics = inMemoryTaskManager.getAllEpics();
        int i = 1;
        for (Epic epic : epics) {
            inMemoryTaskManager.createSubtask(new Subtask(0, i++ + " подзадача (Вторая партия)", "Описание подзадачи", epic.getId()));
        }
    }

}