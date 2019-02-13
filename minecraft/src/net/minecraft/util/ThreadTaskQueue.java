package net.minecraft.util;

import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ThreadTaskQueue<R extends Runnable> implements Executor {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final Queue<R> taskQueue = Queues.<R>newConcurrentLinkedQueue();
	private boolean field_17942 = false;

	@Environment(EnvType.CLIENT)
	public <V> CompletableFuture<V> executeFuture(Supplier<V> supplier) {
		Validate.notNull(supplier);
		return this.isOffThread() ? CompletableFuture.supplyAsync(supplier, this) : CompletableFuture.completedFuture(supplier.get());
	}

	public CompletableFuture<Object> executeFuture(Runnable runnable) {
		Validate.notNull(runnable);
		if (this.isOffThread()) {
			return CompletableFuture.supplyAsync(() -> {
				runnable.run();
				return null;
			}, this);
		} else {
			runnable.run();
			return CompletableFuture.completedFuture(null);
		}
	}

	public void execute(Runnable runnable) {
		if (this.isOffThread()) {
			this.taskQueue.add(this.method_16211(runnable));
		} else {
			runnable.run();
		}
	}

	protected abstract R method_16211(Runnable runnable);

	@Environment(EnvType.CLIENT)
	protected void executeTaskQueue() {
		while (this.executeQueuedTask()) {
		}
	}

	protected boolean executeQueuedTask() {
		R runnable = (R)this.taskQueue.poll();
		if (runnable == null) {
			return false;
		} else {
			this.field_17942 = true;

			try {
				runnable.run();
			} catch (Exception var6) {
				LOGGER.fatal("Error executing task", (Throwable)var6);
			} finally {
				this.field_17942 = false;
			}

			return true;
		}
	}

	protected <T> T method_18248(CompletableFuture<T> completableFuture) {
		while (!completableFuture.isDone()) {
			this.executeQueuedTask();
			Thread.yield();
		}

		return (T)completableFuture.join();
	}

	public boolean isOffThread() {
		return this.field_17942 || !this.isMainThread();
	}

	public abstract boolean isMainThread();
}
