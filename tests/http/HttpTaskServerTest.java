package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import model.enums.ItemType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import model.Epic;
import model.Subtask;
import model.Task;
import util.Managers;

import static model.enums.ItemStatus.NEW;
import static model.enums.ItemType.TASK;


public class HttpTaskServerTest {

    Gson gson = Managers.getGson();
    static KVServer kvServer;
    HttpTaskServer httpTaskServer;

    final Type TASK_TYPE = new TypeToken<Task>() {
    }.getType();
    final Type TASKS_TYPE = new TypeToken<ArrayList<Task>>() {
    }.getType();
    final Type SUBTASK_TYPE = new TypeToken<Subtask>() {
    }.getType();
    final Type SUBTASKS_TYPE = new TypeToken<ArrayList<Subtask>>() {
    }.getType();
    final Type EPIC_TYPE = new TypeToken<Epic>() {
    }.getType();
    final Type EPICS_TYPE = new TypeToken<ArrayList<Epic>>() {
    }.getType();

    static {
        try {
            kvServer = new KVServer();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @BeforeAll
    private static void beforeAll() {
        kvServer.start();
    }

    @AfterAll
    private static void afterAll() {
        kvServer.stop();
    }

    @BeforeEach
    private void beforeEach() {
        try {
            httpTaskServer = new HttpTaskServer();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        httpTaskServer.start();
    }

    @AfterEach
    private void afterEach() {
        httpTaskServer.stop();
    }

    private URI getUrl(String urlPoint) {
        return URI.create("http://localhost:8080/" + urlPoint);
    }

    @Test
    public void getTask() throws IOException, InterruptedException {
        createDemoTasks(1, TASK, null);
        Task createdTask = httpTaskServer.getTaskManager().getTask(1);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/task?id=1")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task httpTask = gson.fromJson(response.body(), TASK_TYPE);
        assertNotNull(httpTask);
        assertEquals(createdTask, httpTask);
    }

    @Test
    public void getSubtask() throws IOException, InterruptedException {
        final Epic epic = httpTaskServer.getTaskManager().createEpic(new Epic(0, "Test createEpic", "Test createEpic description", null, 10, NEW));
        createDemoTasks(1, ItemType.SUBTASK, epic);
        List<Subtask> subTasks = httpTaskServer.getTaskManager().getAllSubtasks();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/subtask?id=2")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task httpSubtask = gson.fromJson(response.body(), SUBTASK_TYPE);
        assertEquals(httpSubtask, subTasks.get(0), "Subtasks не совпадают." + 0);
    }

    @Test
    public void getEpic() throws IOException, InterruptedException {
        createDemoTasks(1, ItemType.EPIC, null);
        List<Epic> epics = httpTaskServer.getTaskManager().getAllEpics();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/epic?id=1")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic httpEpic = gson.fromJson(response.body(), EPIC_TYPE);
        assertEquals(httpEpic, epics.get(0), "Epics не совпадают." + 0);
    }

    @Test
    public void getAllEpics() throws IOException, InterruptedException {
        httpTaskServer.getTaskManager().clearEpics();
        createDemoTasks(1, ItemType.EPIC, null);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/epic")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> httpEpics = gson.fromJson(response.body(), EPICS_TYPE);
        assertEquals(1, httpEpics.size());
    }


    @Test
    public void getAllSubtasks() throws IOException, InterruptedException {
        Epic epic = httpTaskServer.getTaskManager().createEpic(new Epic(0, "Первый Epic", "Описание первого эпика", null, 10));
        httpTaskServer.getTaskManager().createSubtask(
                new Subtask(0, "Test createTask", "Test createTask description", "20.09.2023 15:09:00", 10, NEW, epic.getId()));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/subtask")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> httpSubtasks = gson.fromJson(response.body(), SUBTASKS_TYPE);
        assertEquals(1, httpSubtasks.size());
    }


    @Test
    public void getAllTasks() throws IOException, InterruptedException {
        createDemoTasks(1, ItemType.TASK, null);
        Task createdTask = httpTaskServer.getTaskManager().getTask(1);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/task")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), TASKS_TYPE);
        assertEquals(createdTask, tasks.get(0));
        assertEquals(1, tasks.size());
    }

    @Test
    public void getPriorityTasks() throws IOException, InterruptedException {

        final Task task1 = httpTaskServer.getTaskManager().createTask(new Task(0, "Test createTask1", "Test create task description", "10.07.2023 11:12:18", 10, NEW));
        final Task task2 = httpTaskServer.getTaskManager().createTask(new Task(0, "Test createTask2", "Test create task description", "03.07.2023 14:24:18", 10, NEW));
        final Task task3 = httpTaskServer.getTaskManager().createTask(new Task(0, "Test createTask3", "Test create task description", "28.07.2023 16:19:18", 10, NEW));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), TASKS_TYPE);
        assertEquals(tasks.get(0), task2);
        assertEquals(tasks.get(1), task1);
        assertEquals(tasks.get(2), task3);
    }

    @Test
    public void getHistoryTasks() throws IOException, InterruptedException {
        createDemoTasks(3, ItemType.TASK, null);
        httpTaskServer.getTaskManager().getTask(1);
        httpTaskServer.getTaskManager().getTask(2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/history")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), TASKS_TYPE);
        assertEquals(3, httpTaskServer.getTaskManager().getAllTasks().size());
        assertEquals(2, tasks.size());
    }

    @Test
    public void removeTask() throws IOException, InterruptedException {
        httpTaskServer.getTaskManager().clearTasks();
        createDemoTasks(1, ItemType.TASK, null);
        assertEquals(1, httpTaskServer.getTaskManager().getAllTasks().size());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/task?id=1")).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, httpTaskServer.getTaskManager().getAllTasks().size());
    }

    @Test
    public void removeSubtask() throws IOException, InterruptedException {
        Epic epic = httpTaskServer.getTaskManager().createEpic(new Epic(0, "Первый Epic", "Описание первого эпика", null, 10));
        httpTaskServer.getTaskManager().createSubtask(
                new Subtask(0, "Test createTask", "Test createTask description", "20.09.2023 19:09:00", 10, NEW, epic.getId()));

        assertEquals(1, httpTaskServer.getTaskManager().getAllSubtasks().size());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/subtask?id=2")).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, httpTaskServer.getTaskManager().getAllSubtasks().size());
    }

    @Test
    public void clearSubtasks() throws IOException, InterruptedException {
        httpTaskServer.getTaskManager().clearSubtasks();
        Epic epic = httpTaskServer.getTaskManager().createEpic(new Epic(0, "Первый Epic", "Описание первого эпика", null, 10));
        httpTaskServer.getTaskManager().createSubtask(
                new Subtask(0, "Test createTask", "Test createTask description", "20.09.2023 19:09:00", 10, NEW, epic.getId()));

        assertEquals(1, httpTaskServer.getTaskManager().getAllSubtasks().size());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/subtask")).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, httpTaskServer.getTaskManager().getAllSubtasks().size());
    }

    @Test
    public void removeEpic() throws IOException, InterruptedException {
        httpTaskServer.getTaskManager().clearEpics();
        httpTaskServer.getTaskManager().createEpic(new Epic(0, "Первый Epic", "Описание первого эпика", null, 10));
        assertTrue(httpTaskServer.getTaskManager().getAllEpics().size() > 0);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/epic?id=1")).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, httpTaskServer.getTaskManager().getAllEpics().size());
    }

    @Test
    public void clearEpics() throws IOException, InterruptedException {
        httpTaskServer.getTaskManager().clearEpics();
        httpTaskServer.getTaskManager().createEpic(new Epic(0, "Первый Epic", "Описание первого эпика", null, 10));
        assertEquals(1, httpTaskServer.getTaskManager().getAllEpics().size());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/epic")).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, httpTaskServer.getTaskManager().getAllEpics().size());
    }

    @Test
    public void clearTasks() throws IOException, InterruptedException {
        createDemoTasks(1, ItemType.TASK, null);
        assertEquals(1, httpTaskServer.getTaskManager().getAllTasks().size());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/task")).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, httpTaskServer.getTaskManager().getAllTasks().size());
    }

    @Test
    public void updateTask() throws IOException, InterruptedException {
        createDemoTasks(1, ItemType.TASK, null);
        Task createdTask = httpTaskServer.getTaskManager().getTask(1);
        createdTask.setTitle("demo title");

        String json = gson.toJson(createdTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/task" + createdTask.getId())).POST(body).build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder().uri(getUrl("tasks/task?id=1")).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task httpTask = gson.fromJson(response.body(), TASK_TYPE);
        assertEquals(createdTask, httpTask);
    }

    @Test
    public void updateSubtask() throws IOException, InterruptedException {

        Epic epic = httpTaskServer.getTaskManager().createEpic(new Epic(0, "Первый Epic", "Описание первого эпика", null, 10));
        Subtask subTask = new Subtask(0, "Test createTask", "Test createTask description", "20.06.2023 19:09:00", 10, NEW, epic.getId());

        Subtask createdSubtask = httpTaskServer.getTaskManager().createSubtask(subTask);

        createdSubtask.setTitle("demo title");

        String json = gson.toJson(createdSubtask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/subtask" + createdSubtask.getId())).POST(body).build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder().uri(getUrl("tasks/subtask?id=2")).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask httpSubtask = gson.fromJson(response.body(), SUBTASK_TYPE);
        assertEquals(createdSubtask, httpSubtask);
    }

    @Test
    public void updateEpic() throws IOException, InterruptedException {
        Epic epic = httpTaskServer.getTaskManager().createEpic(new Epic(0, "Первый Epic", "Описание первого эпика", null, 10));
        Epic createdEpic = httpTaskServer.getTaskManager().createEpic(epic);
        createdEpic.setTitle("demo title");

        String json = gson.toJson(createdEpic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/epic" + createdEpic.getId())).POST(body).build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder().uri(getUrl("tasks/epic?id=1")).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic httpEpic = gson.fromJson(response.body(), EPIC_TYPE);
        assertEquals(createdEpic, httpEpic);
    }

    @Test
    void getAllSubtasksByEpicId() throws IOException, InterruptedException {

        createDemoTasks(2, ItemType.EPIC, null);
        Epic epic = httpTaskServer.getTaskManager().getEpic(1);

        int countSubtasks = 3;
        createDemoTasks(countSubtasks, ItemType.SUBTASK, epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/subtask/epic?id=1")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Subtask> httpSubtasks = gson.fromJson(response.body(), SUBTASKS_TYPE);

        assertEquals(countSubtasks, httpSubtasks.size(), "Количество Subtasks не совпадает.");
    }

    @Test
    public void createTask() throws IOException, InterruptedException {
        Task task = getDemoNewTask();
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/task")).POST(body).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task savedTask = gson.fromJson(response.body(), TASK_TYPE);

        assertEquals(response.statusCode(), 201, "Коды не совпадают");
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(1, savedTask.getId(), "Задачи не совпадают.");
    }

    @Test
    public void createEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(0, "Первый Epic", "Описание первого эпика", null, 10);

        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/epic")).POST(body).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic savedEpic = gson.fromJson(response.body(), EPIC_TYPE);

        assertEquals(response.statusCode(), 201, "Коды не совпадают");
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(1, savedEpic.getId(), "Задачи не совпадают.");
    }

    @Test
    public void createSubtask() throws IOException, InterruptedException {
        Epic epic = httpTaskServer.getTaskManager().createEpic(new Epic(0, "Первый Epic", "Описание первого эпика", null, 10));
        Subtask subTask = new Subtask(0, "Test createTask", "Test createTask description", "20.06.2023 13:09:00", 10, NEW, epic.getId());

        String json = gson.toJson(subTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(getUrl("tasks/subtask")).POST(body).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask savedSubtask = gson.fromJson(response.body(), SUBTASK_TYPE);

        assertEquals(response.statusCode(), 201, "Коды не совпадают");
        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(2, savedSubtask.getId(), "Задачи не совпадают.");
    }

    private Task getDemoNewTask() {
        return new Task(0, "Задача с незаполненным временем", "Описание нулевой задачи", null, 10);
    }

    protected void createDemoTasks(int count, ItemType itemType, Epic linkedEpic) {
        for (int i = 0; i < count; i++) {
            String title = String.format("Test creates %s %d", itemType, i);
            String description = String.format("Test creates %s description %d", itemType, i);

            switch (itemType) {
                case TASK:
                    Task task = new Task(i, title, description, String.format("%02d.0%02d.2023 10:00:00", i + 1, i + 1), 10, NEW);
                    httpTaskServer.getTaskManager().createTask(task);
                    break;
                case EPIC:
                    Epic epic = new Epic(i, title, description, null, 10, NEW);
                    httpTaskServer.getTaskManager().createEpic(epic);
                    break;
                case SUBTASK:
                    Subtask subtask = new Subtask(i, title, description, String.format("%02d.0%02d.2023 12:00:00", i + 1, i + 1), 10, NEW, linkedEpic == null ? 0 : linkedEpic.getId());
                    httpTaskServer.getTaskManager().createSubtask(subtask);
                    break;

            }
        }
    }


}