package services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import exception.ManagerSaveException;

import java.util.ArrayList;
import java.util.List;

import http.KVTaskClient;
import model.Epic;
import model.Subtask;
import model.Task;
import model.enums.ItemType;
import util.Managers;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private final String KEY_TASKS = "tasks";
    private final String KEY_HISTORY = "history";
    private final String KEY_PRIORITY = "priority";
    private final Gson gson = Managers.getGson();

    public HttpTaskManager(String url) {
        super(url);
        System.out.println("Конструктор HttpTaskManager");
        kvTaskClient = new KVTaskClient(url);
        System.out.println("Создан клиент в конструкторе HttpTaskManager");
        load();
    }

    @Override
    protected void save() throws ManagerSaveException {
        List<Task> list = new ArrayList<>();
        list.addAll(getAllTasks());
        list.addAll(getAllEpics());
        list.addAll(getAllSubtasks());

        System.out.println("Метод save в HttpTaskManager: сохранение всех задач для ключа " + KEY_TASKS);
        kvTaskClient.put(KEY_TASKS, gson.toJson(list));

        List<Task> history = historyManager.getHistory();
        System.out.println("Метод save в HttpTaskManager: сохранение истории для ключа " + KEY_HISTORY);
        kvTaskClient.put(KEY_HISTORY, gson.toJson(history));

        List<Task> priority = getPrioritizedTasks();
        System.out.println("Метод save в HttpTaskManager: сохранение приоритета задач " + KEY_PRIORITY);
        kvTaskClient.put(KEY_PRIORITY, gson.toJson(priority));
    }


    private void load() {
        loadTasks();
        loadHistory();
        loadPriority();
    }

    private void loadTasks() {
        System.out.println("Метод loadTasks в HttpTaskManager --> tasks");
        String allTasks = kvTaskClient.load(KEY_TASKS);

        if (null == allTasks)
            return;

        JsonElement jsonElementAllTasks = JsonParser.parseString(allTasks);
        if (jsonElementAllTasks.isJsonArray()) {
            JsonArray jsonArrayAllTasks = jsonElementAllTasks.getAsJsonArray();

            for (JsonElement jsonTask : jsonArrayAllTasks) {

                JsonObject jsonObjectTask = jsonTask.getAsJsonObject();
                JsonElement jsonItemType = jsonObjectTask.get("itemType");
                if (jsonItemType.isJsonNull()) {
                    continue;
                }

                ItemType itemType = ItemType.valueOf(jsonItemType.getAsString());

                switch (itemType) {
                    case TASK:
                        Task task = gson.fromJson(jsonObjectTask, Task.class);
                        tasks.put(task.getId(), task);
                        break;
                    case SUBTASK:
                        Subtask subtask = gson.fromJson(jsonObjectTask, Subtask.class);
                        subtasks.put(subtask.getId(), subtask);
                        Epic epic = epics.get(subtask.getEpicId());
                        if (null != epic) {
                            epic.addSubtask(subtask.getId());
                        }
                        break;
                    case EPIC:
                        Epic epicNew = gson.fromJson(jsonObjectTask, Epic.class);
                        epics.put(epicNew.getId(), epicNew);
                }

                JsonElement jsonId = jsonObjectTask.get("id");
                if (jsonId.isJsonNull()){
                    int getId = jsonObjectTask.get("id").getAsInt();
                    if (getId > nextId) {
                        nextId = getId;
                    }
                }

            }
        }
    }

    private void loadHistory() {
        System.out.println("Метод loadHistory в HttpTaskManager --> history");
        String allTasks = kvTaskClient.load(KEY_HISTORY);

        if (null == allTasks)
            return;

        JsonElement jsonElementAllTasks = JsonParser.parseString(allTasks);
        if (jsonElementAllTasks.isJsonArray()) {
            JsonArray jsonArrayAllTasks = jsonElementAllTasks.getAsJsonArray();

            for (JsonElement jsonTask : jsonArrayAllTasks) {

                JsonObject jsonObjectTask = jsonTask.getAsJsonObject();
                JsonElement jsonItemType = jsonObjectTask.get("itemType");
                if (jsonItemType.isJsonNull()) {
                    continue;
                }

                ItemType itemType = ItemType.valueOf(jsonItemType.getAsString());

                switch (itemType) {
                    case TASK:
                        Task task = gson.fromJson(jsonObjectTask, Task.class);
                        historyManager.add(task);
                        break;
                    case SUBTASK:
                        Subtask subtask = gson.fromJson(jsonObjectTask, Subtask.class);
                        historyManager.add(subtask);
                        break;
                    case EPIC:
                        Epic epic = gson.fromJson(jsonObjectTask, Epic.class);
                        historyManager.add(epic);
                }
            }
        }
    }

    private void loadPriority() {
        System.out.println("Метод loadPriority в HttpTaskManager --> priority");
        String allTasks = kvTaskClient.load(KEY_PRIORITY);

        if (null == allTasks)
            return;

        JsonElement jsonElementAllTasks = JsonParser.parseString(allTasks);
        if (jsonElementAllTasks.isJsonArray()) {
            JsonArray jsonArrayAllTasks = jsonElementAllTasks.getAsJsonArray();

            for (JsonElement jsonTask : jsonArrayAllTasks) {

                JsonObject jsonObjectTask = jsonTask.getAsJsonObject();
                JsonElement jsonItemType = jsonObjectTask.get("itemType");
                if (jsonItemType.isJsonNull()) {
                    continue;
                }

                ItemType itemType = ItemType.valueOf(jsonItemType.getAsString());

                switch (itemType) {
                    case TASK:
                        Task task = gson.fromJson(jsonObjectTask, Task.class);
                        prioritizedTasks.add(task);
                        break;
                    case SUBTASK:
                        Subtask subtask = gson.fromJson(jsonObjectTask, Subtask.class);
                        prioritizedTasks.add(subtask);
                        break;
                    case EPIC:
                        Epic epic = gson.fromJson(jsonObjectTask, Epic.class);
                        prioritizedTasks.add(epic);
                }
            }
        }
    }

}
