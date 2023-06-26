package model;

import model.enums.ItemStatus;
import model.enums.ItemType;
import util.Const;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {

    private int id;
    private String title;
    private String description;
    private ItemStatus status;
    private long duration;
    private LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Const.DATE_TIME_FORMAT);

    public Task(int id, String title, String description, LocalDateTime startTime, int duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = ItemStatus.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String title, String description, LocalDateTime startTime, int duration, ItemStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
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
        return getId() == task.getId() && getDuration() == task.getDuration() && getTitle().equals(task.getTitle()) && getDescription().equals(task.getDescription()) && getStatus() == task.getStatus() && getStartTime().equals(task.getStartTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getDescription(), getStatus(), getDuration(), getStartTime());
    }

    @Override
    public String toString() {
        LocalDateTime endTime = this.getEndTime();
        String strEndTime = endTime == null ? "" : endTime.format(dateTimeFormatter);
        return String.format("Task {id = %d, title = %s, description = %s, status = %s; startTime = %s; duration = %d; endTime = %s}",
                id, title, description, status, startTime == null ? "" : startTime.format(dateTimeFormatter), duration, strEndTime);
    }

    public ItemType getType() {
        return ItemType.TASK;
    }

    public LocalDateTime getEndTime() {
        return startTime != null ? startTime.plusMinutes(duration) : null;
    }

}