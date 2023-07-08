package http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exception.IntersectionDateIntervalException;
import model.Epic;
import model.Subtask;
import model.Task;
import services.TaskManager;
import util.Managers;
import util.QueryParameter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Pattern;

import static java.lang.System.out;
import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    private final String TASKS_PATH = "/tasks";
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer server;
    private final TaskManager taskManager;
    private static Gson gson;

    public HttpTaskServer() throws IOException {
        taskManager = Managers.getDefaultHttp("http://localhost:8078");

        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext(TASKS_PATH, this::handleTasks);
    }

    public void start() {
        out.println("Started " + this.getClass().getName() + " " + PORT);
        out.println("http://localhost:" + PORT + TASKS_PATH);
        server.start();
    }

    public void stop() {
        out.println("Останавливаем сервер на порту " + PORT);
        server.stop(1);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    private void handleUnknown(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().toString();
        out.println("Неверный url: " + uri);
        writeResponse(exchange, "Неверный url : " + uri, 404);
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        String data = gson.toJson(tasks, ArrayList.class);
        writeResponse(exchange, data, 200);
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        String data = gson.toJson(subtasks, ArrayList.class);
        writeResponse(exchange, data, 200);
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        String data = gson.toJson(epics, ArrayList.class);
        writeResponse(exchange, data, 200);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        int id = getId(exchange);
        List<Subtask> subtasks = taskManager.getAllSubtasksByEpicId(id);
        String data = gson.toJson(subtasks, ArrayList.class);
        writeResponse(exchange, data, 200);
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getHistory();
        String data = gson.toJson(tasks, ArrayList.class);
        writeResponse(exchange, data, 200);
    }

    private void handleGetPriority(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getPrioritizedTasks();
        String data = gson.toJson(tasks, ArrayList.class);
        writeResponse(exchange, data, 200);
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        int id = getId(exchange);
        out.println("id = " + id);
        Task task = taskManager.getTask(id);
        if (task == null) {
            writeResponse(exchange, "Не найден Task c id = " + id, 404);
        } else {
            String data = gson.toJson(task, Task.class);
            out.println("data = " + data);
            writeResponse(exchange, data, 200);
        }
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {
        int id = getId(exchange);
        out.println("id = " + id);
        Subtask subtask = taskManager.getSubtask(id);
        if (subtask == null) {
            writeResponse(exchange, "Не найден Subtask c id = " + id, 404);
        } else {
            String data = gson.toJson(subtask, Subtask.class);
            out.println("data = " + data);
            writeResponse(exchange, data, 200);
        }
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        int id = getId(exchange);
        out.println("id = " + id);
        Epic epic = taskManager.getEpic(id);
        if (epic == null) {
            writeResponse(exchange, "Не найден Epic c id = " + id, 404);
        } else {
            String data = gson.toJson(epic, Epic.class);
            out.println("data = " + data);
            writeResponse(exchange, data, 200);
        }
    }

    private void handleRemoveTask(HttpExchange exchange) throws IOException {
        int id = getId(exchange);
        out.println("id = " + id);
        if (taskManager.removeTask(id) != null) {
            writeResponse(exchange, "Удален Task с id = " + id, 200);
        } else {
            writeResponse(exchange, "Не найден Task с id = " + id, 400);
        }
    }

    private void handleRemoveEpic(HttpExchange exchange) throws IOException {
        int id = getId(exchange);
        out.println("id = " + id);
        if (taskManager.removeEpic(id) != null) {
            writeResponse(exchange, "Удален Epic с id = " + id, 200);
        } else {
            writeResponse(exchange, "Не найден Epic с id = " + id, 400);
        }
    }

    private void handleRemoveSubtask(HttpExchange exchange) throws IOException {
        int id = getId(exchange);
        out.println("id = " + id);
        if (taskManager.removeSubtask(id) != null) {
            writeResponse(exchange, "Удален Subtask с id = " + id, 200);
        } else {
            writeResponse(exchange, "Не найден Subtask с id = " + id, 400);
        }
    }

    private void handleClearTasks(HttpExchange exchange) throws IOException {
        taskManager.clearTasks();
        writeResponse(exchange, "Удалены Tasks", 200);
    }

    private void handleClearEpics(HttpExchange exchange) throws IOException {
        taskManager.clearEpics();
        writeResponse(exchange, "Удалены Epics", 200);
    }

    private void handleClearSubtasks(HttpExchange exchange) throws IOException {
        taskManager.clearSubtasks();
        writeResponse(exchange, "Удалены Subtasks", 200);
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        String body = "";
        try (InputStream inputStream = exchange.getRequestBody()) {
            body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(body, Task.class);
            if (task != null) {
                if (taskManager.getTask(task.getId()) == null) {
                    Task newTask = taskManager.createTask(task);
                    String data = gson.toJson(newTask, Task.class);
                    writeResponse(exchange, data, 201);
                } else {
                    taskManager.updateTask(task);
                    writeResponse(exchange, "Обновлен Task " + task, 200);
                }
            } else {
                throw new JsonSyntaxException("Не указан экземпляр Task");
            }
        } catch (JsonSyntaxException e) {
            out.println(Arrays.toString(e.getStackTrace()));
            writeResponse(exchange, "Получен некорректный json: " + body + " " + e.getMessage(), 400);
        } catch (IntersectionDateIntervalException e) {
            out.println(Arrays.toString(e.getStackTrace()));
            writeResponse(exchange, "Ошибка : " + e.getMessage(), 400);
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        String body = "";
        try (InputStream inputStream = exchange.getRequestBody()) {
            body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Epic epic = gson.fromJson(body, Epic.class);

            if (epic != null) {
                if (taskManager.getEpic(epic.getId()) == null) {
                    Epic newEpic = taskManager.createEpic(epic);
                    String data = gson.toJson(newEpic, Epic.class);
                    writeResponse(exchange, data, 201);
                } else {
                    taskManager.updateEpic(epic);
                    writeResponse(exchange, "Обновлен Epic " + epic, 200);
                }
            } else {
                throw new JsonSyntaxException("Не указан экземпляр Epic");
            }
        } catch (JsonSyntaxException e) {
            out.println(Arrays.toString(e.getStackTrace()));
            writeResponse(exchange, "Получен некорректный json: " + body + " " + e.getMessage(), 400);
        } catch (IntersectionDateIntervalException e) {
            out.println(Arrays.toString(e.getStackTrace()));
            writeResponse(exchange, "Ошибка : " + e.getMessage(), 400);
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        String body = "";
        try (InputStream inputStream = exchange.getRequestBody()) {
            body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Subtask subtask = gson.fromJson(body, Subtask.class);

            if (subtask != null) {
                if (taskManager.getSubtask(subtask.getId()) == null) {
                    Subtask newSubtask = taskManager.createSubtask(subtask);
                    String data = gson.toJson(newSubtask, Subtask.class);
                    writeResponse(exchange, data, 201);
                } else {
                    taskManager.updateSubtask(subtask);
                    writeResponse(exchange, "Обновлен Subtask " + subtask, 200);
                }
            } else {
                throw new JsonSyntaxException("Не указан экземпляр Subtask");
            }
        } catch (JsonSyntaxException e) {
            out.println(Arrays.toString(e.getStackTrace()));
            writeResponse(exchange, "Получен некорректный json: " + body + " " + e.getMessage(), 400);
        } catch (IntersectionDateIntervalException e) {
            out.println(Arrays.toString(e.getStackTrace()));
            writeResponse(exchange, "Ошибка : " + e.getMessage(), 400);
        }
    }

    private void handleTasks(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().toString();
        Endpoint endpoint = getEndpoint(requestPath, exchange.getRequestMethod());

        out.println("URI = " + exchange.getRequestURI());
        out.println("HttpTaskServer endpoint --> " + endpoint + "; path --> " + requestPath);

        switch (endpoint) {
            case GET_TASKS:
                handleGetTasks(exchange);
                break;
            case GET_TASK:
                handleGetTask(exchange);
                break;
            case POST_TASK:
                handlePostTask(exchange);
                break;
            case REMOVE_TASK:
                handleRemoveTask(exchange);
                break;
            case CLEAR_TASKS:
                handleClearTasks(exchange);
                break;
            case GET_SUBTASKS:
                handleGetSubtasks(exchange);
                break;
            case GET_SUBTASK:
                handleGetSubtask(exchange);
                break;
            case POST_SUBTASK:
                handlePostSubtask(exchange);
                break;
            case REMOVE_SUBTASK:
                handleRemoveSubtask(exchange);
                break;
            case CLEAR_SUBTASKS:
                handleClearSubtasks(exchange);
                break;
            case GET_EPICS:
                handleGetEpics(exchange);
                break;
            case GET_EPIC:
                handleGetEpic(exchange);
                break;
            case POST_EPIC:
                handlePostEpic(exchange);
                break;
            case REMOVE_EPIC:
                handleRemoveEpic(exchange);
                break;
            case CLEAR_EPICS:
                handleClearEpics(exchange);
                break;
            case GET_EPIC_SUBTASKS:
                handleGetEpicSubtasks(exchange);
                break;
            case GET_HISTORY:
                handleGetHistory(exchange);
                break;
            case GET_PRIORITIZED_TASKS:
                handleGetPriority(exchange);
                break;
            default:
                handleUnknown(exchange);
                break;
        }
    }

    private static int getQueryId(String value) {
        String str = value.replaceAll("[^\\d.]", "");
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private static Endpoint getEndpoint(String requestPath, String requestMethod) {
        switch (requestMethod) {
            case "GET":
                if (Pattern.matches("^(.*)/tasks/subtask/epic(.*)?id=\\d+(.?)$", requestPath)) {
                    return Endpoint.GET_EPIC_SUBTASKS;
                } else if (Pattern.matches("^(.*)/tasks/task(.*)?id=\\d+(.?)$", requestPath)) {
                    return Endpoint.GET_TASK;
                } else if (Pattern.matches("^(.*)/tasks/task(.?)$", requestPath)) {
                    return Endpoint.GET_TASKS;
                } else if (Pattern.matches("^(.*)/tasks/subtask(.*)?id=\\d+(.?)$", requestPath)) {
                    return Endpoint.GET_SUBTASK;
                } else if (Pattern.matches("^(.*)/tasks/subtask(.?)$", requestPath)) {
                    return Endpoint.GET_SUBTASKS;
                } else if (Pattern.matches("^(.*)/tasks/epic(.*)?id=\\d+(.?)$", requestPath)) {
                    return Endpoint.GET_EPIC;
                } else if (Pattern.matches("^(.*)/tasks/epic(.?)$", requestPath)) {
                    return Endpoint.GET_EPICS;
                } else if (Pattern.matches("^(.*)/tasks/history(.?)$", requestPath)) {
                    return Endpoint.GET_HISTORY;
                } else if (Pattern.matches("^(.*)/tasks(.?)$", requestPath)) {
                    return Endpoint.GET_PRIORITIZED_TASKS;
                }
                break;
            case "POST":
                if (Pattern.matches("^(.*)/tasks/task(.?)$", requestPath)) {
                    return Endpoint.POST_TASK;
                } else if (Pattern.matches("^(.*)/tasks/subtask(.?)$", requestPath)) {
                    return Endpoint.POST_SUBTASK;
                } else if (Pattern.matches("^(.*)/tasks/epic(.?)$", requestPath)) {
                    return Endpoint.POST_EPIC;
                }
                break;
            case "DELETE":
                if (Pattern.matches("^(.*)/tasks/task(.*)?id=\\d+(.?)$", requestPath)) {
                    return Endpoint.REMOVE_TASK;
                } else if (Pattern.matches("^(.*)/tasks/subtask(.*)?id=\\d+(.?)$", requestPath)) {
                    return Endpoint.REMOVE_SUBTASK;
                } else if (Pattern.matches("^(.*)/tasks/epic(.*)?id=\\d+(.?)$", requestPath)) {
                    return Endpoint.REMOVE_EPIC;
                } else if (Pattern.matches("^(.*)/tasks/task(.?)$", requestPath)) {
                    return Endpoint.CLEAR_TASKS;
                } else if (Pattern.matches("^(.*)/tasks/subtask(.?)$", requestPath)) {
                    return Endpoint.CLEAR_SUBTASKS;
                } else if (Pattern.matches("^(.*)/tasks/epic(.?)$", requestPath)) {
                    return Endpoint.CLEAR_EPICS;
                }
        }
        return Endpoint.UNKNOWN;
    }


    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private int getId(HttpExchange exchange) {
        String path = exchange.getRequestURI().toString();
        String strId = QueryParameter.getQueryValueByKey(path, "id").orElse("");
        return getQueryId(strId);
    }

    enum Endpoint {
        GET_TASKS, GET_TASK, POST_TASK, REMOVE_TASK, CLEAR_TASKS,
        GET_SUBTASKS, GET_SUBTASK, POST_SUBTASK, REMOVE_SUBTASK, CLEAR_SUBTASKS,
        GET_EPICS, GET_EPIC, POST_EPIC, REMOVE_EPIC, CLEAR_EPICS,
        GET_EPIC_SUBTASKS, GET_HISTORY, GET_PRIORITIZED_TASKS, UNKNOWN
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

}
