import model.Epic;
import model.ItemStatus;
import model.Subtask;
import model.Task;
import services.TaskManager;
import util.Managers;

import java.util.List;

public class Main {

    private final TaskManager taskManager = Managers.getDefault();

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        System.out.println("\nЗадачи (tasks)");
        createDemoTasks();
        printTasks();
        printHistory();

        System.out.println("\nДобавили подзадачи (subtasks)");
        createDemoEpics();
        printSubtasks();

        System.out.println("Эпики (epics)");
        printEpics();
        printHistory();

        System.out.println("\nИзменились статусы всех подзадач на DONE");
        changeSubtasksStatus();
        System.out.println("Подзадачи (subtasks) после смены статусов");
        printSubtasks();
        System.out.println("Эпики (epics) после смены статусов");
        printEpics();
        printHistory();

        System.out.println("\nУдаление подзадач");
        taskManager.clearSubtasks();
        System.out.println("Подзадачи (subtasks) после удаления");
        printSubtasks();
        System.out.println("Эпики (epics) после удаления подзадач");
        printEpics();
        printHistory();

        System.out.println("\nДобавили новую порцию подзадач");
        addDemoSubtasks();
        System.out.println("Подзадачи (subtasks) после нового добавления");
        printSubtasks();
        System.out.println("Эпики (epics) после добавления подзадач");
        printEpics();
        printHistory();

        System.out.println("\nУдаляем все Эпики");
        System.out.println("*** Остались эпики после удаления ***");
        taskManager.clearEpics();
        printEpics();
        printHistory();

        System.out.println("### Subtasks после удаления Эпиков ###");
        printSubtasks();
    }

    private void changeSubtasksStatus() {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        for (Subtask subtask : subtasks) {
            subtask.setStatus(ItemStatus.DONE);
            taskManager.updateSubtask(subtask);
        }
    }

    private void createDemoEpics() {
        Epic epic1 = taskManager.createEpic(new Epic(0, "Первый Epic", "Описание первого эпика"));
        Epic epic2 = taskManager.createEpic(new Epic(0, "Второй Epic", "Описание второго эпика"));
        Epic epic3 = taskManager.createEpic(new Epic(0, "Третий Epic", "Описание третьего эпика"));

        taskManager.createSubtask(new Subtask(0, "Первая подзадача", "Описание первой подзадачи", epic1.getId()));
        taskManager.createSubtask(new Subtask(0, "Вторая подзадача", "Описание второй подзадачи", epic2.getId()));
        taskManager.createSubtask(new Subtask(0, "Третья подзадача", "Описание третьей подзадачи", epic3.getId()));
    }

    private void createDemoTasks() {
        taskManager.createTask(new Task(0, "Первая задача", "Описание первой задачи"));
        taskManager.createTask(new Task(0, "Вторая задача", "Описание второй задачи"));
        taskManager.createTask(new Task(0, "Третья задача", "Описание третьей задачи"));
    }

    private void addDemoSubtasks() {
        List<Epic> epics = taskManager.getAllEpics();
        int i = 1;
        for (Epic epic : epics) {
            taskManager.createSubtask(new Subtask(0, i++ + " подзадача (Вторая партия)", "Описание подзадачи", epic.getId()));
        }
    }

    private void printTasks() {
        List<Task> list = taskManager.getAllTasks();

        list.forEach(task -> {
            taskManager.getTask(task.getId());
            System.out.println(task);
        });
    }

    private void printEpics() {
        List<Epic> list = taskManager.getAllEpics();
        list.forEach(epic -> {
            taskManager.getEpic(epic.getId());
            System.out.println(epic);
        });
    }

    private void printSubtasks() {
        List<Subtask> list = taskManager.getAllSubtasks();
        list.forEach(subtask -> {
            taskManager.getSubtask(subtask.getId());
            System.out.println(subtask);
        });
    }

    private void printHistory() {
        List<Task> tasks = taskManager.getHistory();

        System.out.println("*** Начало истории просмотров ***");

        if (null != tasks) {
            tasks.forEach(task -> {
                System.out.println(task);
            });
        }
        System.out.println("*** Завершение истории просмотров ***");

    }

}