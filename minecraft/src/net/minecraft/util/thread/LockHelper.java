package net.minecraft.util.thread;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockHelper {
	private static final Logger LOGGER = LogManager.getLogger();
	private final String name;
	private final Semaphore semaphore = new Semaphore(1);
	private final Lock lock = new ReentrantLock();
	@Nullable
	private volatile Thread thread;
	@Nullable
	private volatile CrashException crashException;

	public LockHelper(String name) {
		this.name = name;
	}

	public void lock() {
		boolean bl = false;

		try {
			this.lock.lock();
			if (!this.semaphore.tryAcquire()) {
				this.thread = Thread.currentThread();
				bl = true;
				this.lock.unlock();

				try {
					this.semaphore.acquire();
				} catch (InterruptedException var6) {
					Thread.currentThread().interrupt();
				}

				throw this.crashException;
			}
		} finally {
			if (!bl) {
				this.lock.unlock();
			}
		}
	}

	public void unlock() {
		try {
			this.lock.lock();
			Thread thread = this.thread;
			if (thread != null) {
				CrashException crashException = crash(this.name, thread);
				this.crashException = crashException;
				this.semaphore.release();
				throw crashException;
			}

			this.semaphore.release();
		} finally {
			this.lock.unlock();
		}
	}

	public static CrashException crash(String message, @Nullable Thread thread) {
		String string = (String)Stream.of(Thread.currentThread(), thread).filter(Objects::nonNull).map(LockHelper::method_39936).collect(Collectors.joining("\n"));
		String string2 = "Accessing " + message + " from multiple threads";
		CrashReport crashReport = new CrashReport(string2, new IllegalStateException(string2));
		CrashReportSection crashReportSection = crashReport.addElement("Thread dumps");
		crashReportSection.add("Thread dumps", string);
		LOGGER.error("Thread dumps: \n" + string);
		return new CrashException(crashReport);
	}

	private static String method_39936(Thread thread) {
		return thread.getName() + ": \n\tat " + (String)Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "));
	}
}
