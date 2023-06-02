package services;

import exception.ManagerSaveException;
import model.Subtask;
import model.Epic;
import model.Task;
import model.enums.ItemType;
import model.enums.ItemStatus;

import java.io.BufferedReader;
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

public final class FileBackedTasksManager extends InMemoryTaskManager {

    private final String filePath;
    private final static String DELIMITER = ",";
    private final static List<String> HEADERS = List.of("id", "type", "name", "status", "description", "epic");

    public FileBackedTasksManager(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public Task createTask(Task task) {
        task = super.createTask(task);
        save();
        return task;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic = super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void removeEpic(int epicId) {
        super.removeEpic(epicId);
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        subtask = super.createSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    public static String historyToString(HistoryManager manager) {
        List<Task> list = manager.getHistory();
        return list == null ? "" : list.stream().map(task -> String.valueOf(task.getId())).collect(Collectors.joining(DELIMITER));
    }

    public static List<Integer> historyFromString(String value) {
        return Arrays.stream(value.split(DELIMITER))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    public static FileBackedTasksManager loadFromFile(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("Файл не существует: " + file.getAbsolutePath());
        }

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file.getPath());
        long count = Files.lines(Path.of(file.getPath())).count();

        try (Reader reader = new FileReader(file)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            int i = 0;
            while (bufferedReader.ready()) {

                String line = bufferedReader.readLine();

                if (i > 0 && i < count - 2) {
                    Task task = fromString(line);

                    if (task == null)
                        continue;

                    if (task instanceof Subtask) {
                        Subtask subtask = (Subtask) task;
                        fileBackedTasksManager.subtasks.put(task.getId(), subtask);
                        Epic epic = fileBackedTasksManager.epics.get(subtask.getEpicId());
                        if (null != epic) {
                            epic.addSubtask(subtask.getId());
                        }
                    } else if (task instanceof Epic) {
                        fileBackedTasksManager.epics.put(task.getId(), (Epic) task);
                    } else {
                        fileBackedTasksManager.tasks.put(task.getId(), task);
                    }
                    fileBackedTasksManager.nextId = task.getId() + 1;

                } else if (i == count - 1) {
                    List<Integer> historyTaskIds = historyFromString(line);
                    historyTaskIds.forEach(taskId -> {

                        Task task = fileBackedTasksManager.tasks.get(taskId);
                        if (task == null) {
                            task = fileBackedTasksManager.subtasks.get(taskId);
                            if (task == null) {
                                task = fileBackedTasksManager.epics.get(taskId);
                            }
                        }
                        if (task != null) {
                            fileBackedTasksManager.historyManager.add(task);
                        }

                    });
                }
                i++;
            }
        } catch (IOException ex) {
            System.out.println("message IOException: " + ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            System.out.println("message Exception: " + ex.getMessage());
            throw ex;
        }

        return fileBackedTasksManager;
    }

    private String toString(final Task task) {
        return String.join(DELIMITER,
                String.valueOf(task.getId()),
                String.valueOf(task.getType()),
                task.getTitle(),
                String.valueOf(task.getStatus()),
                task.getDescription(),
                task instanceof Subtask ? String.valueOf(((Subtask) task).getEpicId()) : ""
        );
    }

    private static Task fromString(String value) {
        String[] fields = value.split(DELIMITER);

        if (fields.length >= HEADERS.size() - 1) {

            int id = Integer.parseInt(fields[0]);
            ItemType itemType = ItemType.valueOf(fields[1]);
            String name = fields[2];
            ItemStatus itemStatus = ItemStatus.valueOf(fields[3]);
            String description = fields[4];
            int epicId = fields.length == HEADERS.size() ? Integer.parseInt(fields[5]) : 0;

            switch (itemType) {
                case TASK:
                    return new Task(id, name, description, itemStatus);
                case EPIC:
                    return new Epic(id, name, description, itemStatus);
                case SUBTASK:
                    return new Subtask(id, name, description, itemStatus, epicId);
            }
        }
        return null;
    }

    private void save() throws ManagerSaveException {
        List<Task> list = new ArrayList<>();
        list.addAll(getAllTasks());
        list.addAll(getAllEpics());
        list.addAll(getAllSubtasks());

        try (BufferedWriter br = new BufferedWriter(new FileWriter(filePath))) {
            br.write(String.join(DELIMITER, HEADERS));
            br.newLine();
            for (Task task : list) {
                br.write(toString(task));
                br.newLine();
            }
            br.newLine();
            br.write(historyToString(historyManager));
            br.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException(e.getMessage());
        }
    }
}
