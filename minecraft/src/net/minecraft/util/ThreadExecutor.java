package net.minecraft.util;

import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ThreadExecutor<R extends Runnable> implements Actor<R>, Executor {
	private final String name;
	private static final Logger LOGGER = LogManager.getLogger();
	private final Queue<R> taskQueue = Queues.<R>newConcurrentLinkedQueue();
	private int waitCount;

	protected ThreadExecutor(String string) {
		this.name = string;
	}

	protected abstract R prepareRunnable(Runnable runnable);

	protected abstract boolean canRun(R runnable);

	public boolean isOnThread() {
		return Thread.currentThread() == this.getThread();
	}

	protected abstract Thread getThread();

	protected boolean isOffThread() {
		return !this.isOnThread();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Environment(EnvType.CLIENT)
	public <V> CompletableFuture<V> executeFuture(Supplier<V> supplier) {
		return this.isOffThread() ? CompletableFuture.supplyAsync(supplier, this) : CompletableFuture.completedFuture(supplier.get());
	}

	private CompletableFuture<Object> executeFuture(Runnable runnable) {
		return CompletableFuture.supplyAsync(() -> {
			runnable.run();
			return null;
		}, this);
	}

	public void method_19537(Runnable runnable) {
		if (!this.isOnThread()) {
			this.executeFuture(runnable).join();
		} else {
			runnable.run();
		}
	}

	public void method_18858(R runnable) {
		this.taskQueue.add(runnable);
		LockSupport.unpark(this.getThread());
	}

	public void execute(Runnable runnable) {
		if (this.isOffThread()) {
			this.method_18858(this.prepareRunnable(runnable));
		} else {
			runnable.run();
		}
	}

	@Environment(EnvType.CLIENT)
	protected void clear() {
		this.taskQueue.clear();
	}

	protected void executeTaskQueue() {
		while (this.executeQueuedTask()) {
		}
	}

	protected boolean executeQueuedTask() {
		R runnable = (R)this.taskQueue.peek();
		if (runnable == null) {
			return false;
		} else if (this.waitCount == 0 && !this.canRun(runnable)) {
			return false;
		} else {
			this.runSafely((R)this.taskQueue.remove());
			return true;
		}
	}

	public void waitFor(BooleanSupplier booleanSupplier) {
		this.waitCount++;

		try {
			while (!booleanSupplier.getAsBoolean()) {
				if (!this.executeQueuedTask()) {
					LockSupport.parkNanos("waiting for tasks", 1000L);
				}
			}
		} finally {
			this.waitCount--;
		}
	}

	protected void runSafely(R runnable) {
		try {
			runnable.run();
		} catch (Exception var3) {
			LOGGER.fatal("Error executing task on {}", this.getName(), var3);
		}
	}
}
