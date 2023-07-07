package services;

import model.Epic;
import model.Subtask;
import model.Task;
import model.enums.ItemType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Managers;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends TaskManagerTest {

    private final String FILE_PATH = "resources/test_tasks.csv";

    @BeforeEach
    private void BeforeEach() {
        taskManager = Managers.getDefaultFile(FILE_PATH);
    }

    @Test
    public void loadFromFile() {

        createDemoTasks();
        createDemoEpics();

        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getSubtask(5);
        taskManager.getTask(1);

        try {
            FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.load(Paths.get(FILE_PATH).toFile());
            List<Task> tasks = fileBackedTasksManager.getAllTasks();
            assertEquals(2, tasks.size(), "Неверное количество Tasks");

            List<Epic> epics = fileBackedTasksManager.getAllEpics();
            assertEquals(2, epics.size(), "Неверное количество Epics");

            List<Subtask> subtasks = fileBackedTasksManager.getAllSubtasks();
            assertEquals(3, subtasks.size(), "Неверное количество Subtasks");

            List<Task> taskHistories = fileBackedTasksManager.getHistory();

            assertEquals(3, taskHistories.size(), "Неверное количество в истории");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDemoTasks() {
        taskManager.createTask(new Task(0, "Первая задача", "Описание первой задачи", "20.06.2023 13:15:00", 10));
        taskManager.createTask(new Task(0, "Вторая задача", "Описание второй задачи", "20.06.2023 13:30:00", 10));
    }

    private void createDemoEpics() {
        Epic epic1 = taskManager.createEpic(new Epic(0, "Первый Epic", "Описание первого эпика", null, 10));
        taskManager.createSubtask(new Subtask(0, "Первая подзадача", "Описание первой подзадачи", "20.06.2023 14:01:00", 10, epic1.getId()));
        taskManager.createSubtask(new Subtask(0, "Вторая подзадача", "Описание второй подзадачи", "20.06.2023 14:19:00", 10, epic1.getId()));
        taskManager.createSubtask(new Subtask(0, "Третья подзадача", "Описание третьей подзадачи", "20.06.2023 15:09:00", 10, epic1.getId()));

        taskManager.createEpic(new Epic(0, "Второй Epic", "Описание второго эпика", null, 10));
    }

}
