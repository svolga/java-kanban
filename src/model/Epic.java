package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private final List<Integer> subtaskIds = new ArrayList<>();

    public Epic(int id, String title, String description) {
        super(id, title, description);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtask(int id) {
        subtaskIds.add(id);
    }

    public void removeSubtask(int id) {
        Iterator<Integer> values = subtaskIds.iterator();
        while (values.hasNext()) {
            int value = values.next();
            if (id == value) {
                values.remove();
            }
        }
    }

    public void removeAllSubtasks() {
        subtaskIds.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(getSubtaskIds(), epic.getSubtaskIds());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSubtaskIds());
    }

    @Override
    public String toString() {
        return "Epic{" +
                super.toString() +
                ", subtasks=" + String.valueOf(subtaskIds) +
                '}';
    }

}
