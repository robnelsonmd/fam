package fam.core.executor;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.concurrent.ScheduledFuture;

public interface TaskScheduler {
    ScheduledFuture<Void> scheduleDailyTask(ExecutableTask task, int hour, int minute, int second);
    ScheduledFuture<Void> scheduleDailyTask(ExecutableTask task, ZoneId zoneId, int hour, int minute, int second);
    ScheduledFuture<Void> scheduleWeeklyTask(ExecutableTask task, DayOfWeek day, int hour, int minute, int second);
    ScheduledFuture<Void> scheduleWeeklyTask(ExecutableTask task, ZoneId zoneId, DayOfWeek day, int hour, int minute, int second);
    void shutdown();
}
