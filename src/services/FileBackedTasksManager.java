package services;

import exception.ManagerSaveException;
import model.Subtask;
import model.Epic;
import model.Task;
import model.ItemType;
import model.ItemStatus;

import java.io.BufferedReader;
import java.io.Writer;
import java.io.Reader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class FileBackedTasksManager extends InMemoryTaskManager implements StorageManager {

    private final String filePath;
    private final static String DELIMITER = ",";
    private final static List<String> HEADERS = List.of ("id", "type", "name", "status", "description", "epic");

    public FileBackedTasksManager(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask (id);
        save ();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic (id);
        save ();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask (id);
        save ();
        return subtask;
    }

    @Override
    public Task createTask(Task task) {
        task = super.createTask (task);
        save ();
        return task;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask (task);
        save ();
    }

    @Override
    public void clearTasks() {
        super.clearTasks ();
        save ();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask (id);
        save ();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks ();
        save ();
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic = super.createEpic (epic);
        save ();
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic (epic);
        save ();
    }

    @Override
    public void clearEpics() {
        super.clearEpics ();
        save ();
    }

    @Override
    public void removeEpic(int epicId) {
        super.removeEpic (epicId);
        save ();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        subtask = super.createSubtask (subtask);
        save ();
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask (subtask);
        save ();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask (id);
        save ();
    }

    @Override
    public void save() throws ManagerSaveException {
        List<Task> list = new ArrayList<> ();
        list.addAll (getAllTasks ());
        list.addAll (getAllEpics ());
        list.addAll (getAllSubtasks ());

        try (Writer writer = new FileWriter (filePath)) {
            BufferedWriter br = new BufferedWriter (writer);
            br.write (String.join (DELIMITER, HEADERS));
            br.newLine ();
            for (Task task : list) {
                br.write (toString (task));
                br.newLine ();
            }
            br.newLine ();
            br.write (historyToString (historyManager));
            br.flush ();
        } catch (IOException e) {
            e.printStackTrace ();
            throw new ManagerSaveException (e.getMessage ());
        }
    }

    public static String historyToString(HistoryManager manager) {
        List<Task> list = manager.getHistory ();
        return list == null ? "" : list.stream ().map (Task::getId).map (String::valueOf).collect (Collectors.joining (DELIMITER));
    }

    public static List<Integer> historyFromString(String value) {
        return Arrays.stream (value.split (DELIMITER))
                .map (String::trim)
                .filter (s -> !s.isEmpty ())
                .map (Integer::valueOf)
                .collect (Collectors.toList ());
    }

    public static FileBackedTasksManager loadFromFile(File file) throws IOException {
        if (!file.exists ()) {
            throw new FileNotFoundException ("Файл не существует: " + file.getAbsolutePath ());
        }

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager (file.getPath ());
        Map<Integer, Task> tasks = new LinkedHashMap<> ();
        long count = Files.lines (Path.of (file.getPath ())).count ();

        try (Reader reader = new FileReader (file)) {
            BufferedReader bufferedReader = new BufferedReader (reader);
            int i = 0;
            while (bufferedReader.ready ()) {

                String line = bufferedReader.readLine ();

                if (i > 0 && i < count - 2) {
                    Task task = fromString (line);

                    if (task == null)
                        continue;

                    if (task instanceof Subtask)
                        fileBackedTasksManager.createSubtask ((Subtask) task);
                    else if (task instanceof Epic)
                        fileBackedTasksManager.createEpic ((Epic) task);
                    else
                        fileBackedTasksManager.createTask (task);

                    tasks.put (task.getId (), task);

                } else if (i == count - 1) {
                    List<Integer> historyTaskIds = historyFromString (line);
                    historyTaskIds.forEach (taskId -> {
                        Task task = tasks.get (taskId);
                        if (task != null) {
                            fileBackedTasksManager.historyManager.add (tasks.get (taskId));
                        }
                    });

                }
                i++;
            }
        } catch (IOException ex) {
            System.out.println ("message IOException: " + ex.getMessage ());
            throw ex;
        } catch (Exception ex) {
            System.out.println ("message Exception: " + ex.getMessage ());
            throw ex;
        }

        return fileBackedTasksManager;
    }

    private String toString(final Task task) {
        return String.join (DELIMITER,
                String.valueOf (task.getId ()),
                String.valueOf (task.getItemType ()),
                task.getTitle (),
                String.valueOf (task.getStatus ()),
                task.getDescription (),
                task instanceof Subtask ? String.valueOf (((Subtask) task).getEpicId ()) : ""
        );
    }

    private static Task fromString(String value) {
        String[] fields = value.split (DELIMITER);

        if (fields.length >= HEADERS.size () - 1) {

            int id = 0;
            ItemType itemType = null;
            String name = "";
            ItemStatus itemStatus = null;
            String description = "";
            int epicId = 0;

            for (int i = 0; i < fields.length; i++) {
                switch (i) {
                    case 0:
                        id = Integer.parseInt (fields[i]);
                        break;
                    case 1:
                        itemType = ItemType.valueOf (fields[i]);
                        break;
                    case 2:
                        name = fields[i];
                        break;
                    case 3:
                        itemStatus = ItemStatus.valueOf (fields[i]);
                        break;
                    case 4:
                        description = fields[i];
                        break;
                    case 5:
                        epicId = Integer.parseInt (fields[i]);
                        break;
                }
            }

            switch (itemType) {
                case TASK:
                    return new Task (id, name, description, itemStatus);
                case EPIC:
                    return new Epic (id, name, description);
                case SUBTASK:
                    return new Subtask (id, name, description, itemStatus, epicId);
            }
        }
        return null;
    }

}
