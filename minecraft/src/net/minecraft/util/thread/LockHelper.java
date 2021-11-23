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
	private static final Logger field_36293 = LogManager.getLogger();
	private final String field_36294;
	private final Semaphore field_36295 = new Semaphore(1);
	private final Lock field_36296 = new ReentrantLock();
	@Nullable
	private volatile Thread field_36297;
	@Nullable
	private volatile CrashException field_36298;

	public LockHelper(String string) {
		this.field_36294 = string;
	}

	public void method_39935() {
		boolean bl = false;

		try {
			this.field_36296.lock();
			if (!this.field_36295.tryAcquire()) {
				this.field_36297 = Thread.currentThread();
				bl = true;
				this.field_36296.unlock();

				try {
					this.field_36295.acquire();
				} catch (InterruptedException var6) {
					Thread.currentThread().interrupt();
				}

				throw this.field_36298;
			}
		} finally {
			if (!bl) {
				this.field_36296.unlock();
			}
		}
	}

	public void method_39937() {
		try {
			this.field_36296.lock();
			Thread thread = this.field_36297;
			if (thread != null) {
				CrashException crashException = crash(this.field_36294, thread);
				this.field_36298 = crashException;
				this.field_36295.release();
				throw crashException;
			}

			this.field_36295.release();
		} finally {
			this.field_36296.unlock();
		}
	}

	public static CrashException crash(String message, @Nullable Thread thread) {
		String string = (String)Stream.of(Thread.currentThread(), thread).filter(Objects::nonNull).map(LockHelper::method_39936).collect(Collectors.joining("\n"));
		CrashReport crashReport = new CrashReport("Accessing " + message + " from multiple threads", new IllegalStateException());
		CrashReportSection crashReportSection = crashReport.addElement("Thread dumps");
		crashReportSection.add("Thread dumps", string);
		field_36293.error("Thread dumps: \n" + string);
		return new CrashException(crashReport);
	}

	private static String method_39936(Thread thread) {
		return thread.getName() + ": \n\tat " + (String)Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "));
	}
}
