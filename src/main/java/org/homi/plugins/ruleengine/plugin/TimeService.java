package org.homi.plugins.ruleengine.plugin;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeService {
	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public static boolean nowIsBetween(String startTime, String endTime) {
		var now = LocalTime.now();
		return now.isAfter(LocalTime.parse(startTime)) && now.isBefore(LocalTime.parse(endTime));
	}

	public static LocalDateTime now() {
		return LocalDateTime.now();
	}
	
	public static void scheduleTask(Runnable task, long delay) {
		scheduler.schedule(task, delay, TimeUnit.MILLISECONDS);
	}
	
	public static void scheduleRecurringTask(Runnable task, long time, TimeUnit timeUnit) {
		scheduler.scheduleWithFixedDelay(task, time, 1, timeUnit);
	}
}
