package fam.core.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TaskSchedulerImpl implements TaskScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(TaskSchedulerImpl.class);

    private final ScheduledExecutorService executorService;

    public TaskSchedulerImpl(int threadCount) {
        this.executorService = Executors.newScheduledThreadPool(threadCount);
    }

    @Override
    public ScheduledFuture<Void> scheduleDailyTask(ExecutableTask task, int hour, int minute, int second) {
        return scheduleDailyTask(task, ZoneId.systemDefault(), hour, minute, second);
    }

    @Override
    public ScheduledFuture<Void> scheduleDailyTask(ExecutableTask task, ZoneId zoneId, int hour, int minute, int second) {
        long delay = computeNextDelay(zoneId, hour, minute, second);
        LOG.info(String.format("Scheduling daily task (%s) with current delay of %d seconds",task.getClass().getSimpleName(),delay));

        return (ScheduledFuture<Void>) executorService.schedule(() -> {
            try {
                task.execute();
                scheduleDailyTask(task, zoneId, hour, minute, second);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delay, TimeUnit.SECONDS);
    }

    @Override
    public ScheduledFuture<Void> scheduleWeeklyTask(ExecutableTask task, DayOfWeek day, int hour, int minute, int second) {
        return scheduleWeeklyTask(task, ZoneId.systemDefault(), day, hour, minute, second);
    }

    @Override
    public ScheduledFuture<Void> scheduleWeeklyTask(ExecutableTask task, ZoneId zoneId, DayOfWeek day, int hour, int minute, int second) {
        long delay = computeNextDelay(zoneId, day, hour, minute, second);
        LOG.info(String.format("Scheduling weekly task (%s) with current delay of %d seconds",task.getClass().getSimpleName(),delay));

        return (ScheduledFuture<Void>) executorService.schedule(() -> {
            try {
                task.execute();
                scheduleWeeklyTask(task, zoneId, day, hour, minute, second);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delay, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {
        executorService.shutdownNow();
    }

    private static long computeNextDelay(ZoneId zoneId, int hour, int minute, int second)
    {
        ZonedDateTime currentDateTime = getCurrentDateTime(zoneId);
        ZonedDateTime scheduledDateTime = getScheduledDateTime(zoneId, hour, minute, second);

        Duration duration = Duration.between(currentDateTime, scheduledDateTime);
        duration = (duration.getSeconds() <= 0) ? duration.plusDays(1) : duration;

        return duration.getSeconds();
    }

    private static long computeNextDelay(ZoneId zoneId, DayOfWeek dayOfWeek, int hour, int minute, int second)
    {
        ZonedDateTime currentDateTime = getCurrentDateTime(zoneId);
        ZonedDateTime scheduledDateTime = getScheduledDateTime(zoneId, hour, minute, second);

        int dayOfWeekDelta = getDayOfWeekDelta(currentDateTime, scheduledDateTime, dayOfWeek);
        scheduledDateTime = scheduledDateTime.plusDays(dayOfWeekDelta);

        Duration duration = Duration.between(currentDateTime, scheduledDateTime);
        duration = (duration.getSeconds() <= 0) ? duration.plusDays(7) : duration;

        return duration.getSeconds();
    }

    private static ZonedDateTime getCurrentDateTime(ZoneId zoneId) {
        return ZonedDateTime.now(zoneId);
    }

    private static int getDayOfWeekDelta(ZonedDateTime currentDateTime,
                                         ZonedDateTime nextDateTime, DayOfWeek nextDayOfWeek) {
        DayOfWeek currentDayOfWeek = currentDateTime.getDayOfWeek();
        int delta = nextDayOfWeek.getValue() - currentDayOfWeek.getValue();

        if (delta > 0) {
            return delta;
        }

        if (delta < 0) {
            return (7 - Math.abs(delta));
        }

        if (nextDateTime.isBefore(currentDateTime)) {
            return 7;
        }

        return 0;
    }

    private static ZonedDateTime getScheduledDateTime(ZoneId zoneId, int hour, int minute, int second) {
        ZonedDateTime scheduledDateTime = getCurrentDateTime(zoneId);
        return scheduledDateTime.withHour(hour).withMinute(minute).withSecond(second);
    }
}
