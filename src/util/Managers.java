package util;

import services.HistoryManager;
import services.InMemoryHistoryManager;
import services.InMemoryTaskManager;
import services.TaskManager;
import services.FileBackedTasksManager;

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

}
