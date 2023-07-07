package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import services.HttpTaskManager;
import http.LocalDateTimeAdapter;
import services.HistoryManager;
import services.InMemoryHistoryManager;
import services.InMemoryTaskManager;
import services.TaskManager;
import services.FileBackedTasksManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Managers {

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefaultFile(String filePath) {
        return new FileBackedTasksManager(filePath);
    }

    public static HttpTaskManager getDefaultHttp(String url){
        return new HttpTaskManager(url);
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
}
