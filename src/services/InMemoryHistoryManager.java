package services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.Node;
import model.Task;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> tasks = new CustomLinkedList<>();

    class CustomLinkedList<T extends Task> {

        private final Map<Integer, Node> map = new LinkedHashMap<>();
        private Node<T> head;
        private Node<T> tail;

        public void linkLast(T t) {
            final Node<T> newNode = new Node<>(tail, t, null);
            if (tail == null)
                head = newNode;
            else
                tail.next = newNode;

            tail = newNode;
            map.put(t.getId(), newNode);
        }

        public List<Task> getTasks() {
            return map.values().stream().map(node -> node.data).collect(Collectors.toList());
        }

        public void removeNode(int id) {
            Node node = map.remove(id);

            if (null == node)
                return;

            Node prev = node.prev;
            Node next = node.next;

            if (null != prev) {
                prev.next = next;
            } else {
                head = next;
            }

            if (null != next) {
                next.prev = prev;
            } else {
                tail = prev;
            }
        }
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            tasks.linkLast(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(tasks.getTasks());
    }

    @Override
    public void remove(int id) {
        tasks.removeNode(id);
    }
}
