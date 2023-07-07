package model;

import model.enums.ItemStatus;
import model.enums.ItemType;
import util.Const;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class Task {
    private int id;
    private String title;
    protected ItemType itemType;
    private String description;
    private ItemStatus status;
    private long duration;
    private LocalDateTime startTime;
    protected LocalDateTime endTime;


    public Task(int id, String title, String description, String startTime, int duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = ItemStatus.NEW;
        this.duration = duration;
        this.itemType = ItemType.TASK;
        try {
            this.startTime = (startTime != null && !startTime.isEmpty()) ? LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern(Const.DATE_TIME_FORMAT)) : null;
        } catch (DateTimeParseException ignored) {
        }

    }

    public Task(int id, String title, String description, String startTime, int duration, ItemStatus status) {
        this(id, title, description, startTime, duration);
        this.itemType = ItemType.TASK;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return getId() == task.getId() &&
                getItemType() == task.getItemType() &&
                getDuration() == task.getDuration() &&
                getTitle().equals(task.getTitle()) &&
                getDescription().equals(task.getDescription()) &&
                getStatus() == task.getStatus() &&
                Objects.equals(getStartTime(), task.getStartTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getItemType(), getTitle(), getDescription(), getStatus(), getDuration(), getStartTime());
    }

    @Override
    public String toString() {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Const.DATE_TIME_FORMAT);
        LocalDateTime endTime = this.getEndTime();
        String strEndTime = endTime == null ? "" : endTime.format(dateTimeFormatter);
        return String.format("Task {id = %d, itemType = %s, title = %s, description = %s, status = %s; startTime = %s; duration = %d; endTime = %s}",
                id, itemType, title, description, status, startTime == null ? "" : startTime.format(dateTimeFormatter), duration, strEndTime);
    }

    public ItemType getItemType() {
        return itemType;
    }

    public LocalDateTime getEndTime() {
        return startTime != null ? startTime.plusMinutes(duration) : null;
    }

}