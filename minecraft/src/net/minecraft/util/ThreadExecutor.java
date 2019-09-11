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
	private int field_18319;

	protected ThreadExecutor(String string) {
		this.name = string;
	}

	protected abstract R createTask(Runnable runnable);

	protected abstract boolean canExecute(R runnable);

	public boolean isOnThread() {
		return Thread.currentThread() == this.getThread();
	}

	protected abstract Thread getThread();

	protected boolean shouldExecuteAsync() {
		return !this.isOnThread();
	}

	public int getTaskQueueSize() {
		return this.taskQueue.size();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Environment(EnvType.CLIENT)
	public <V> CompletableFuture<V> supply(Supplier<V> supplier) {
		return this.shouldExecuteAsync() ? CompletableFuture.supplyAsync(supplier, this) : CompletableFuture.completedFuture(supplier.get());
	}

	private CompletableFuture<Void> createFuture(Runnable runnable) {
		return CompletableFuture.supplyAsync(() -> {
			runnable.run();
			return null;
		}, this);
	}

	public CompletableFuture<Void> method_20493(Runnable runnable) {
		if (this.shouldExecuteAsync()) {
			return this.createFuture(runnable);
		} else {
			runnable.run();
			return CompletableFuture.completedFuture(null);
		}
	}

	public void executeSync(Runnable runnable) {
		if (!this.isOnThread()) {
			this.createFuture(runnable).join();
		} else {
			runnable.run();
		}
	}

	public void method_18858(R runnable) {
		this.taskQueue.add(runnable);
		LockSupport.unpark(this.getThread());
	}

	public void execute(Runnable runnable) {
		if (this.shouldExecuteAsync()) {
			this.method_18858(this.createTask(runnable));
		} else {
			runnable.run();
		}
	}

	@Environment(EnvType.CLIENT)
	protected void clearTasks() {
		this.taskQueue.clear();
	}

	protected void executeQueuedTasks() {
		while (this.executeQueuedTask()) {
		}
	}

	protected boolean executeQueuedTask() {
		R runnable = (R)this.taskQueue.peek();
		if (runnable == null) {
			return false;
		} else if (this.field_18319 == 0 && !this.canExecute(runnable)) {
			return false;
		} else {
			this.executeTask((R)this.taskQueue.remove());
			return true;
		}
	}

	public void executeTasks(BooleanSupplier booleanSupplier) {
		this.field_18319++;

		try {
			while (!booleanSupplier.getAsBoolean()) {
				if (!this.executeQueuedTask()) {
					this.waitForTasks();
				}
			}
		} finally {
			this.field_18319--;
		}
	}

	protected void waitForTasks() {
		Thread.yield();
		LockSupport.parkNanos("waiting for tasks", 100000L);
	}

	protected void executeTask(R runnable) {
		try {
			runnable.run();
		} catch (Exception var3) {
			LOGGER.fatal("Error executing task on {}", this.getName(), var3);
		}
	}
}
