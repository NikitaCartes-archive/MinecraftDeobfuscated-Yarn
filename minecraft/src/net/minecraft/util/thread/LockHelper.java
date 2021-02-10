package net.minecraft.util.thread;

import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public class LockHelper {
	public static void checkLock(Semaphore semaphore, @Nullable AtomicStack<Pair<Thread, StackTraceElement[]>> lockStack, String message) {
		boolean bl = semaphore.tryAcquire();
		if (!bl) {
			throw crash(message, lockStack);
		}
	}

	public static CrashException crash(String message, @Nullable AtomicStack<Pair<Thread, StackTraceElement[]>> lockStack) {
		String string = (String)Thread.getAllStackTraces()
			.keySet()
			.stream()
			.filter(Objects::nonNull)
			.map(thread -> thread.getName() + ": \n\tat " + (String)Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat ")))
			.collect(Collectors.joining("\n"));
		CrashReport crashReport = new CrashReport("Accessing " + message + " from multiple threads", new IllegalStateException());
		CrashReportSection crashReportSection = crashReport.addElement("Thread dumps");
		crashReportSection.add("Thread dumps", string);
		if (lockStack != null) {
			StringBuilder stringBuilder = new StringBuilder();

			for (Pair<Thread, StackTraceElement[]> pair : lockStack.toList()) {
				stringBuilder.append("Thread ")
					.append(pair.getFirst().getName())
					.append(": \n\tat ")
					.append((String)Arrays.stream(pair.getSecond()).map(Object::toString).collect(Collectors.joining("\n\tat ")))
					.append("\n");
			}

			crashReportSection.add("Last threads", stringBuilder.toString());
		}

		return new CrashException(crashReport);
	}
}
