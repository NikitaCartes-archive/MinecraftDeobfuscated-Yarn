package net.minecraft.util.thread;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public class LockHelper {
	public static void checkLock(ReentrantLock lock, String message) {
		if (lock.isLocked() && !lock.isHeldByCurrentThread()) {
			throw crash(message);
		} else {
			lock.lock();
		}
	}

	public static CrashException crash(String message) {
		String string = (String)Thread.getAllStackTraces()
			.keySet()
			.stream()
			.filter(Objects::nonNull)
			.map(thread -> thread.getName() + ": \n\tat " + (String)Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat ")))
			.collect(Collectors.joining("\n"));
		CrashReport crashReport = new CrashReport("Accessing " + message + " from multiple threads", new IllegalStateException());
		CrashReportSection crashReportSection = crashReport.addElement("Thread dumps");
		crashReportSection.add("Thread dumps", string);
		return new CrashException(crashReport);
	}
}
