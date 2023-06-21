package services;

import exception.IntersectionDateIntervalException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.enums.ItemType;
import org.junit.jupiter.api.Test;
import util.Const;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static model.enums.ItemStatus.IN_PROGRESS;
import static model.enums.ItemStatus.NEW;
import static model.enums.ItemStatus.DONE;
import static org.junit.jupiter.api.Assertions.*;

abstract public class TaskManagerTest<T extends TaskManager> {

    T taskManager;
    protected final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Const.DATE_TIME_FORMAT);

    @Test
    void createTask() {
        Task task = new Task(0, "Test createTask", "Test createTask description", LocalDateTime.parse("20.06.2023 13:09:00", dateTimeFormatter), 10, NEW);
        final Task savedTask = taskManager.createTask(task);

        assertEquals(1, savedTask.getId(), "Неверный Id для Task");
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateTask() {
        Task task = new Task(1, "Test createTask", "Test createTask description", LocalDateTime.parse("20.06.2023 11:15:00", dateTimeFormatter), 10, NEW);
        final Task savedTask = taskManager.createTask(task);
        savedTask.setTitle("demo title");
        savedTask.setStatus(IN_PROGRESS);
        savedTask.setDescription("demo desc");

        taskManager.updateTask(savedTask);
        final List<Task> tasks = taskManager.getAllTasks();
        assertEquals(savedTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getTask() {
        Task task = new Task(1, "Test createTask", "Test createTask description", LocalDateTime.parse("20.06.2023 13:09:00", dateTimeFormatter), 10, NEW);
        final Task savedTask = taskManager.createTask(task);
        assertEquals(savedTask, taskManager.getAllTasks().get(0), "Задачи не совпадают.");
    }

    @Test
    void getAllTasks() {
        int count = 3;
        createDemoTasks(count, ItemType.TASK, null);
        final List<Task> tasks = taskManager.getAllTasks();
        assertEquals(count, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void clearTasks() {
        int count = 3;
        createDemoTasks(count, ItemType.TASK, null);
        final List<Task> tasks = taskManager.getAllTasks();
        assertEquals(count, tasks.size(), "Неверное количество задач.");

        taskManager.clearTasks();
        final List<Task> tasks2 = taskManager.getAllTasks();
        assertEquals(0, tasks2.size(), "Задачи не удаляются");
    }

    @Test
    void removeTask() {
        int count = 1;
        createDemoTasks(count, ItemType.TASK, null);
        final List<Task> tasks = taskManager.getAllTasks();
        assertEquals(count, tasks.size(), "Неверное количество задач.");

        taskManager.removeTask(1);
        assertEquals(0, taskManager.getAllTasks().size(), "Задачи не удаляются");
    }

    @Test
    void clearSubtasks() {
        Epic epic = taskManager.createEpic(new Epic(0, "Первый Epic", "Описание первого эпика"));
        assertNotNull(epic, "Epic не существует");

        Subtask subTask = taskManager.createSubtask(new Subtask(0, "Первый Subtask", "Описание первого Subtask", LocalDateTime.parse("20.06.2023 13:09:00", dateTimeFormatter), 10, epic.getId()));
        assertNotNull(subTask, "SubTask не существует");
        assertEquals(subTask.getEpicId(), epic.getId(), "Неверно назначается Epic");
        assertEquals(1, epic.getSubtaskIds().size(), "Неверное количество Subtasks внутри Epic");
        assertEquals(1, taskManager.getAllSubtasks().size(), "Неверное количество Subtasks в taskManager");

        taskManager.clearSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size(), "Неверное количество Subtasks после удаления");
        assertEquals(0, epic.getSubtaskIds().size(), "Неверное количество Subtasks внутри Epic после удаления");
    }

    @Test
    void createEpic() {
        Epic epic = taskManager.createEpic(new Epic(0, "Первый Epic", "Описание первого эпика"));

        assertEquals(1, epic.getId(), "Неверный Id для Epic");
        assertEquals(0, epic.getSubtaskIds().size(), "Неверное количество подзадач");
        assertEquals(NEW, epic.getStatus(), "Неверный статус для нового Epic " + NEW);

        int count = 3;
        createDemoTasks(count, ItemType.SUBTASK, epic);
        final List<Integer> subTaskIds = epic.getSubtaskIds();

        assertEquals(count, subTaskIds.size(), "Неверное количество подзадач");

        for (Subtask subTask : taskManager.getAllSubtasks()) {
            assertEquals(NEW, subTask.getStatus(), "Неверный статус для Subtask" + subTask);
        }

        for (Subtask subTask : taskManager.getAllSubtasks()) {
            subTask.setStatus(IN_PROGRESS);
            taskManager.updateSubtask(subTask);
        }
        assertEquals(IN_PROGRESS, epic.getStatus(), "Неверный статус для Epic " + IN_PROGRESS);

        for (Subtask subTask : taskManager.getAllSubtasks()) {
            subTask.setStatus(DONE);
            taskManager.updateSubtask(subTask);
        }
        assertEquals(DONE, epic.getStatus(), "Неверный статус для Epic " + DONE);

        int i = 0;
        for (Subtask subTask : taskManager.getAllSubtasks()) {
            subTask.setStatus(i == 0 ? NEW : DONE);
            taskManager.updateSubtask(subTask);
            i++;
        }
        assertEquals(IN_PROGRESS, epic.getStatus(), "Неверный статус для Epic " + IN_PROGRESS);
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic(0, "Test createEpic", "Test createEpic description", NEW);
        final Epic savedEpic = taskManager.createEpic(epic);
        savedEpic.setTitle("demo title");
        savedEpic.setStatus(IN_PROGRESS);
        savedEpic.setDescription("demo desc");

        taskManager.updateEpic(savedEpic);
        final List<Epic> epics = taskManager.getAllEpics();
        assertEquals(savedEpic, epics.get(0), "Epics не совпадают.");
    }

    @Test
    void getEpic() {
        createDemoTasks(3, ItemType.EPIC, null);
        List<Epic> epics = taskManager.getAllEpics();

        assertEquals(epics.get(0), taskManager.getEpic(1), "Epic не совпадают." + 0);
        assertEquals(epics.get(1), taskManager.getEpic(2), "Epic не совпадают." + 1);
        assertEquals(epics.get(2), taskManager.getEpic(3), "Epic не совпадают." + 2);
    }

    @Test
    void getAllEpics() {
        int count = 3;
        createDemoTasks(count, ItemType.EPIC, null);
        assertEquals(count, taskManager.getAllEpics().size(), "Количество Epics не совпадает.");
    }

    @Test
    void clearEpics() {
        Epic epic = taskManager.createEpic(new Epic(0, "Первый Epic", "Описание первого эпика"));

        int createdEpicId = epic.getId();
        int count = 3;
        createDemoTasks(count, ItemType.SUBTASK, epic);

        assertEquals(1, taskManager.getAllEpics().size(), "Количество Epics неверное");
        assertEquals(count, epic.getSubtaskIds().size(), "Количество Subtasks неверное");

        taskManager.clearEpics();
        assertEquals(0, taskManager.getAllEpics().size(), "Количество Epics неверное");
        assertEquals(0, taskManager.getAllSubtasksByEpicId(createdEpicId).size(), "Количество Subtasks неверное");
    }

    @Test
    void removeEpic() {
        Epic epic = taskManager.createEpic(new Epic(0, "Первый Epic", "Описание первого эпика"));

        int createdEpicId = epic.getId();
        int count = 3;
        createDemoTasks(count, ItemType.SUBTASK, epic);

        assertEquals(1, taskManager.getAllEpics().size(), "Количество Epics неверное");
        assertEquals(count, epic.getSubtaskIds().size(), "Количество Subtasks неверное");

        taskManager.removeEpic(createdEpicId);
        assertEquals(0, taskManager.getAllEpics().size(), "Количество Epics неверное");
        assertEquals(0, taskManager.getAllSubtasksByEpicId(createdEpicId).size(), "Количество Subtasks неверное");
    }

    @Test
    void createSubtask() {
        Epic epic = taskManager.createEpic(new Epic(0, "Первый Epic", "Описание первого эпика"));

        Subtask subTask = new Subtask(0, "Test createTask", "Test createTask description", LocalDateTime.parse("20.06.2023 13:09:00", dateTimeFormatter), 10, NEW, epic.getId());
        final Subtask savedSubTask = taskManager.createSubtask(subTask);

        assertEquals(2, savedSubTask.getId(), "Неверный Id для SubTask");
        assertNotNull(savedSubTask, "Задача не найдена.");
        assertEquals(subTask, savedSubTask, "Задачи не совпадают.");

        final List<Subtask> subTasks = taskManager.getAllSubtasks();

        assertNotNull(subTasks, "Задачи на возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask, subTasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic(0, "Test createEpic", "Test createEpic description", NEW);
        final Epic savedEpic = taskManager.createEpic(epic);

        Subtask subTask = new Subtask(0, "Test createTask", "Test createTask description", LocalDateTime.parse("20.06.2023 13:09:00", dateTimeFormatter), 10, NEW, savedEpic.getId());
        final Subtask savedSubTask = taskManager.createSubtask(subTask);
        savedSubTask.setTitle("demo title");
        savedSubTask.setStatus(IN_PROGRESS);
        savedSubTask.setDescription("demo desc");

        taskManager.updateSubtask(savedSubTask);
        final List<Subtask> subTasks = taskManager.getAllSubtasks();
        assertEquals(savedSubTask, subTasks.get(0), "Subtasks не совпадают.");
    }

    @Test
    void getAllSubtasks() {
        final Epic epic = taskManager.createEpic(new Epic(0, "Test createEpic", "Test createEpic description", NEW));

        int count = 3;
        createDemoTasks(count, ItemType.SUBTASK, epic);
        assertEquals(count, taskManager.getAllSubtasks().size(), "Количество Subtasks не совпадает.");
    }

    @Test
    void getAllSubtasksByEpicId() {

        createDemoTasks(2, ItemType.EPIC, null);
        Epic epic = taskManager.getEpic(1);

        int countSubtasks = 3;
        createDemoTasks(countSubtasks, ItemType.SUBTASK, epic);

        List<Subtask> subtasks = taskManager.getAllSubtasksByEpicId(epic.getId());
        assertEquals(countSubtasks, subtasks.size(), "Количество Subtasks не совпадает.");
    }

    @Test
    void getSubtask() {
        final Epic epic = taskManager.createEpic(new Epic(0, "Test createEpic", "Test createEpic description", NEW));
        createDemoTasks(3, ItemType.SUBTASK, epic);
        List<Subtask> subTasks = taskManager.getAllSubtasks();

        assertEquals(subTasks.get(0), taskManager.getSubtask(2), "Subtasks не совпадают." + 0);
        assertEquals(subTasks.get(1), taskManager.getSubtask(3), "Subtasks не совпадают." + 1);
        assertEquals(subTasks.get(2), taskManager.getSubtask(4), "Subtasks не совпадают." + 2);
    }

    @Test
    void removeSubtask() {
        final int count = 5;
        final Epic epic = taskManager.createEpic(new Epic(0, "Test createEpic", "Test createEpic description", NEW));
        createDemoTasks(count, ItemType.SUBTASK, epic);
        assertEquals(count, taskManager.getAllSubtasks().size());

        int id = 2;
        Subtask subTask = taskManager.getSubtask(id);
        assertNotNull(subTask, "Subtask не равно null");

        taskManager.removeSubtask(id);
        Subtask removedSubTask = taskManager.getSubtask(id);
        assertNull(removedSubTask, "Subtask равен null");

        assertEquals(count, taskManager.getAllSubtasks().size() + 1);
    }

    @Test
    void getHistory() {
        int count = 5;
        createDemoTasks(count, ItemType.TASK, null);
        final List<Task> tasks = taskManager.getAllTasks();
        assertEquals(count, tasks.size(), "Неверное количество задач.");

        List<Integer> idsForHistory = List.of(1, 2, 3, 5, 1, 4);
        for (Integer id : idsForHistory) {
            taskManager.getTask(id);
        }
        List<Task> histories = taskManager.getHistory();
        assertEquals(5, histories.size(), "Неверное кол-во просмотров истории");

        assertEquals(2, histories.get(0).getId(), "Неверное кол-во просмотров истории");
        assertEquals(4, histories.get(4).getId(), "Неверное кол-во просмотров истории");
        assertEquals(1, histories.get(3).getId(), "Неверное кол-во просмотров истории");

    }

    protected void createDemoTasks(int count, ItemType itemType, Epic linkedEpic) {

        for (int i = 0; i < count; i++) {

            String title = String.format("Test creates %s %d", itemType, i);
            String description = String.format("Test creates %s description %d", itemType, i);

            switch (itemType) {
                case TASK:
                    Task task = new Task(i, title, description, LocalDateTime.parse(String.format("%02d.06.2023 10:00:00", i + 1), dateTimeFormatter), 10, NEW);
                    taskManager.createTask(task);
                    break;
                case EPIC:
                    Epic epic = new Epic(i, title, description, NEW);
                    taskManager.createEpic(epic);
                    break;
                case SUBTASK:
                    Subtask subtask = new Subtask(i, title, description, LocalDateTime.parse(String.format("%02d.06.2023 12:00:00", i + 1), dateTimeFormatter), 10, NEW, linkedEpic == null ? 0 : linkedEpic.getId());
                    taskManager.createSubtask(subtask);
                    break;

            }
        }
    }

    @Test
    public void shouldThrowExceptionWhenIntersectionDateInterval() {
        IntersectionDateIntervalException ex = assertThrows(
                IntersectionDateIntervalException.class,
                () -> {
                    taskManager.createTask(new Task(0, "Первая задача", "Описание первой задачи", LocalDateTime.parse("25.06.2023 12:05:00", dateTimeFormatter), 10));
                    taskManager.createTask(new Task(0, "Вторая задача с пересечением времени", "Описание второй задачи", LocalDateTime.parse("25.06.2023 12:04:00", dateTimeFormatter), 10));
                }
        );

        assertEquals("Ошибка: Пересечение интервалов в задаче: Task {id = 0, title = Вторая задача с пересечением времени, description = Описание второй задачи, status = NEW; startTime = 25.06.2023 12:04:00; duration = 10; endTime = 25.06.2023 12:14:00} c Task {id = 1, title = Первая задача, description = Описание первой задачи, status = NEW; startTime = 25.06.2023 12:05:00; duration = 10; endTime = 25.06.2023 12:15:00}", ex.getMessage());
    }

}
