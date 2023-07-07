package model;

import model.enums.ItemStatus;
import model.enums.ItemType;
import java.util.Objects;

public class Subtask extends Task {

   final private int epicId;

    public Subtask(int id, String title, String description, String startTime, int duration, int epicId) {
        super(id, title, description, startTime, duration);
        this.epicId = epicId;
        this.itemType = ItemType.SUBTASK;
    }

    public Subtask(int id, String title, String description, String startTime, int duration, ItemStatus status, int epicId) {
        super(id, title, description, startTime, duration, status);
        this.epicId = epicId;
        this.itemType = ItemType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask)) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return getEpicId() == subtask.getEpicId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEpicId());
    }

    @Override
    public String toString() {
        return "Subtask{" +
                super.toString() +
                ", epicId=" + epicId +
                '}';
    }
}
