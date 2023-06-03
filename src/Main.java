import model.Epic;
import model.Subtask;
import model.Task;
import services.FileBackedTasksManager;
import services.TaskManager;
import util.Managers;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    private final String FILE_PATH = "resources/task.csv";
    private final TaskManager taskManager = Managers.getDefaultFile (FILE_PATH);

    public static void main(String[] args) {
        new Main ().run ();
    }

    private void run() {
        createDemo ();

        try {
            FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile (Paths.get (FILE_PATH).toFile ());
            List<Task> tasks = fileBackedTasksManager.getAllTasks ();
            List<Epic> epics = fileBackedTasksManager.getAllEpics ();
            List<Subtask> subtasks = fileBackedTasksManager.getAllSubtasks ();
            List<Task> taskHistories = fileBackedTasksManager.getHistory ();

            System.out.println ("\n ************ После чтения из файла: ***********");
            tasks.forEach (System.out::println);
            epics.forEach (System.out::println);
            subtasks.forEach (System.out::println);

            System.out.println ("\n ############ После чтения Истории из файла: ############");
            taskHistories.forEach (System.out::println);

        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    private void createDemo() {
        System.out.println ("\nЗадачи (tasks)");
        createDemoTasks ();
        printTasks ();
        printHistory ();

        System.out.println ("\nПросмотрели задачу task --> 1");
        taskManager.getTask (1);
        printHistory ();

        System.out.println ("\nДобавили подзадачи (subtasks)");
        createDemoEpics ();

        printSubtasks ();
        printHistory ();
    }

    private void createDemoEpics() {
        Epic epic1 = taskManager.createEpic (new Epic (0, "Первый Epic", "Описание первого эпика"));
        taskManager.createSubtask (new Subtask (0, "Первая подзадача", "Описание первой подзадачи", epic1.getId ()));
        taskManager.createSubtask (new Subtask (0, "Вторая подзадача", "Описание второй подзадачи", epic1.getId ()));
        taskManager.createSubtask (new Subtask (0, "Третья подзадача", "Описание третьей подзадачи", epic1.getId ()));

        taskManager.createEpic (new Epic (0, "Второй Epic", "Описание второго эпика"));
    }

    private void createDemoTasks() {
        taskManager.createTask (new Task (0, "Первая задача", "Описание первой задачи"));
        taskManager.createTask (new Task (0, "Вторая задача", "Описание второй задачи"));
    }

    private void printTasks() {
        System.out.println ("\nПросмотрели все tasks");
        List<Task> list = taskManager.getAllTasks ();

        list.forEach (task -> {
            taskManager.getTask (task.getId ());
            System.out.println (task);
        });
    }

    private void printEpics() {
        System.out.println ("\nПросмотрели все эпики");
        List<Epic> list = taskManager.getAllEpics ();

        list.forEach (epic -> {
            taskManager.getEpic (epic.getId ());
            System.out.println (epic);
        });
    }

    private void printSubtasks() {
        System.out.println ("\nПросмотрели все subtasks");

        List<Subtask> list = taskManager.getAllSubtasks ();
        list.forEach (subtask -> {
            taskManager.getSubtask (subtask.getId ());
            System.out.println (subtask);
        });
    }

    private void printHistory() {
        List<Task> tasks = taskManager.getHistory ();

        System.out.println ("\n*** Начало истории просмотров ***");

        if (null != tasks) {
            tasks.forEach (System.out::println);
        }
        System.out.println ("*** Завершение истории просмотров ***");
    }

}