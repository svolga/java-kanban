package model;

import java.util.*;

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

    public void removeSubtask(Integer id) {
        subtaskIds.remove(id);
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
