package services;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Const;
import util.Managers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static model.enums.ItemStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryHistoryManagerTest {

    HistoryManager historyManager;
    protected final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Const.DATE_TIME_FORMAT);

    @BeforeEach
    private void BeforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        Task task = new Task(0, "Test createTask", "Test createTask description", LocalDateTime.parse("22.06.2023 13:09:10", dateTimeFormatter), 8, NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void addShouldNotReturnDoubles() {
        Task task = new Task(0, "Test createTask", "Test createTask description", LocalDateTime.parse("20.06.2023 13:09:00", dateTimeFormatter), 10, NEW);
        for (int i = 0; i < 3; i++) {
            historyManager.add(task);
        }
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Дублей в истории нет");
    }

    @Test
    void remove() {
        List<Task> tasks = new ArrayList<>();
        final int count = 4;
        for (int i = 0; i < count; i++) {
            Task task = new Task(i, "Test createTask" + i, "Test createTask description" + i, LocalDateTime.parse("21.06.2023 13:12:00", dateTimeFormatter), 10, NEW);
            tasks.add(task);
            historyManager.add(task);
        }

        final List<Task> history = historyManager.getHistory();
        int[] testIndexTasks = {0, count - 2, count - 1};

        for (int i = 0; i < testIndexTasks.length; i++) {
            historyManager.remove(tasks.get(testIndexTasks[i]).getId());
            final List<Task> historyNew = historyManager.getHistory();
            assertEquals(history.size() - i - 1, historyNew.size(), "История удалена для Task с индексом " + i);
        }
    }

}