package net.minecraft.util.thread;

import com.mojang.logging.LogUtils;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;

/**
 * A custom thread factory that assigns each created thread to the group of the
 * system security manager or the factory-creating thread (when the security
 * manager does not exist). Otherwise, it behaves much like the thread creation
 * logic in {@link net.minecraft.util.Util#createIoWorker()}.
 */
public class GroupAssigningThreadFactory implements ThreadFactory {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final ThreadGroup group;
	private final AtomicInteger nextIndex = new AtomicInteger(1);
	private final String prefix;

	public GroupAssigningThreadFactory(String name) {
		SecurityManager securityManager = System.getSecurityManager();
		this.group = securityManager != null ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
		this.prefix = name + "-";
	}

	public Thread newThread(Runnable r) {
		Thread thread = new Thread(this.group, r, this.prefix + this.nextIndex.getAndIncrement(), 0L);
		thread.setUncaughtExceptionHandler((threadx, throwable) -> {
			LOGGER.error("Caught exception in thread {} from {}", threadx, r);
			LOGGER.error("", throwable);
		});
		if (thread.getPriority() != 5) {
			thread.setPriority(5);
		}

		return thread;
	}
}
