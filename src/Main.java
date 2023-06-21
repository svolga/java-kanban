import model.Epic;
import model.Subtask;
import model.Task;
import services.FileBackedTasksManager;
import services.TaskManager;
import util.Const;
import util.Managers;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {

    private final String FILE_PATH = "resources/task.csv";
    private final TaskManager taskManager = Managers.getDefaultFile (FILE_PATH);
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Const.DATE_TIME_FORMAT);

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

            System.out.println ("\n ############ Список задач в порядке приоритета: ############");
            taskManager.getPrioritizedTasks().forEach(System.out::println);

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
        taskManager.createSubtask (new Subtask (0, "Первая подзадача", "Описание первой подзадачи", LocalDateTime.parse("14.06.2023 09:15:00", dateTimeFormatter),  5, epic1.getId ()));

//        taskManager.createSubtask (new Subtask (0, "Первая подзадача - попытка пересечения времени", "Описание первой подзадачи22", LocalDateTime.parse("14.06.2023 09:16:00", dateTimeFormatter),  5, epic1.getId ()));

        taskManager.createSubtask (new Subtask (0, "Вторая подзадача", "Описание второй подзадачи", LocalDateTime.parse("15.06.2023 18:23:00", dateTimeFormatter), 15, epic1.getId ()));
        taskManager.createSubtask (new Subtask (0, "Третья подзадача", "Описание третьей подзадачи", LocalDateTime.parse("29.06.2023 15:10:00", dateTimeFormatter), 8, epic1.getId ()));

        taskManager.createEpic (new Epic (0, "Второй Epic", "Описание второго эпика"));
    }

    private void createDemoTasks() {
        taskManager.createTask (new Task (0, "Задача с незаполненным временем", "Описание нулевой задачи", null, 10));
        taskManager.createTask (new Task (0, "Первая задача", "Описание первой задачи", LocalDateTime.parse("25.06.2023 12:05:00", dateTimeFormatter), 10));

//        taskManager.createTask (new Task (0, "Первая задача2 - - попытка пересечения времени", "Описание первой задачи2", LocalDateTime.parse("25.06.2023 12:04:00", dateTimeFormatter), 10));

        taskManager.createTask (new Task (0, "Задача с незаполненным временем2", "Описание нулевой задачи2", null, 10));
        taskManager.createTask (new Task (0, "Вторая задача", "Описание второй задачи", LocalDateTime.parse("27.06.2023 14:34:00", dateTimeFormatter), 7));
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