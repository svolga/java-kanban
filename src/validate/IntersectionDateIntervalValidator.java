package validate;

import exception.IntersectionDateIntervalException;
import model.Task;

import java.time.LocalDateTime;
import java.util.Set;

public class IntersectionDateIntervalValidator<T extends Task> {

    public void validate(Set<T> setTasks, T checkedTask) throws IntersectionDateIntervalException {
        if (setTasks != null && checkedTask != null) {

            LocalDateTime checkedStartTime = checkedTask.getStartTime();
            if (checkedStartTime != null) {

                LocalDateTime checkedEndTime = checkedTask.getEndTime();
                for (T task : setTasks) {

                    if (task.getId() == checkedTask.getId())
                        continue;

                    LocalDateTime startTime = task.getStartTime();
                    if (startTime != null) {
                        LocalDateTime endTime = task.getEndTime();
                        if (((startTime.isBefore(checkedEndTime) || startTime.isEqual(checkedEndTime)) && endTime.isAfter(checkedEndTime)) ||
                                (startTime.isAfter(checkedStartTime) && (endTime.isBefore(checkedEndTime) || endTime.isEqual(checkedEndTime))) ||
                                ((startTime.isBefore(checkedStartTime) || startTime.isEqual(checkedStartTime)) && (endTime.isAfter(checkedStartTime)))) {
                            throw new IntersectionDateIntervalException(String.format("Ошибка: Пересечение интервалов в задаче: %s c %s", checkedTask, task));
                        }
                    }

                }
            }
        }
    }
}
