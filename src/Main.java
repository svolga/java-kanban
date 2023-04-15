import model.Epic;
import model.ItemStatus;
import model.Subtask;
import model.Task;
import services.EpicManager;
import services.SubtaskManager;
import services.TaskManager;

import java.util.List;

public class Main {

    private final TaskManager taskManager = new TaskManager();
    private final EpicManager epicManager = new EpicManager();
    private final SubtaskManager subtaskManager = new SubtaskManager(epicManager);

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        System.out.println("\nЗадачи (tasks)");
        createDemoTasks();
        taskManager.print();

        System.out.println("\nДобавили подзадачи (subtasks)");
        createDemoEpics();
        subtaskManager.print();
        System.out.println("Эпики (epics)");
        epicManager.print();

        System.out.println("\nИзменились статусы всех подзадач на DONE");
        changeSubtasksStatus();
        System.out.println("Подзадачи (subtasks) после смены статусов");
        subtaskManager.print();
        System.out.println("Эпики (epics) после смены статусов");
        epicManager.print();

        System.out.println("\nУдаление подзадач");
        subtaskManager.clear();
        System.out.println("Подзадачи (subtasks) после удаления");
        subtaskManager.print();
        System.out.println("Эпики (epics) после удаления подзадач");
        epicManager.print();

        System.out.println("\nДобавили новую порцию подзадач");
        addDemoSubtasks();
        System.out.println("Подзадачи (subtasks) после нового добавления");
        subtaskManager.print();
        System.out.println("Эпики (epics) после добавления подзадач");
        epicManager.print();

        System.out.println("\nУдаляем все Эпики");
        System.out.println("*** Остались эпики после удаления ***");
        epicManager.clear();
        epicManager.print();

        System.out.println("### Subtasks после удаления Эпиков ###");
        subtaskManager.print();
    }

    private void changeSubtasksStatus() {
        List<Subtask> subtasks = subtaskManager.getAll();
        for (Subtask subtask : subtasks) {
            subtask.setStatus(ItemStatus.DONE);
            subtaskManager.update(subtask);
        }
    }

    private void createDemoEpics() {
        Epic epic1 = epicManager.create(new Epic(0, "Первый Epic", "Описание первого эпика"));
        Epic epic2 = epicManager.create(new Epic(0, "Второй Epic", "Описание второго эпика"));

        subtaskManager.create(new Subtask(0, "Первая подзадача", "Описание первой подзадачи", epic1.getId()));
        subtaskManager.create(new Subtask(0, "Вторая подзадача", "Описание второй подзадачи", epic1.getId()));
        subtaskManager.create(new Subtask(0, "Третья подзадача", "Описание третьей подзадачи", epic2.getId()));
    }

    private void createDemoTasks() {
        taskManager.create(new Task(0, "Первая задача", "Описание первой задачи"));
        taskManager.create(new Task(0, "Вторая задача", "Описание второй задачи"));
    }

    private void addDemoSubtasks() {
        List<Epic> epics = epicManager.getAll();
        int i = 1;
        for (Epic epic : epics) {
            subtaskManager.create(new Subtask(0, i + " подзадача (Вторая партия)", "Описание подзадачи", epic.getId()));
            i++;
        }
    }

}