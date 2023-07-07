package model;

import model.enums.ItemStatus;
import model.enums.ItemType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private final List<Integer> subtaskIds = new ArrayList<>();

    public Epic(int id, String title, String description, String startTime, int duration) {
        super(id, title, description, startTime, duration);
        this.itemType = ItemType.EPIC;
    }

    public Epic(int id, String title, String description, String startTime, int duration, ItemStatus itemStatus) {
        super(id, title, description, startTime, duration, itemStatus);
        this.itemType = ItemType.EPIC;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtask(int id) {
        subtaskIds.add(id);
    }

    public void removeSubtask(int id) {
        subtaskIds.remove((Integer) id);
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
                ", subtasks=" + subtaskIds +
                '}';
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }
}
