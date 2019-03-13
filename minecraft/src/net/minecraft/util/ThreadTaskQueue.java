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

public abstract class ThreadTaskQueue<R extends Runnable> implements Actor<R>, Executor {
	private final String field_18318;
	private static final Logger LOGGER = LogManager.getLogger();
	private final Queue<R> taskQueue = Queues.<R>newConcurrentLinkedQueue();
	private int field_18319;

	protected ThreadTaskQueue(String string) {
		this.field_18318 = string;
	}

	protected abstract R method_16211(Runnable runnable);

	protected abstract boolean method_18856(R runnable);

	public boolean method_18854() {
		return Thread.currentThread() == this.method_3777();
	}

	protected abstract Thread method_3777();

	protected boolean isOffThread() {
		return !this.method_18854();
	}

	@Override
	public String getName() {
		return this.field_18318;
	}

	@Environment(EnvType.CLIENT)
	public <V> CompletableFuture<V> executeFuture(Supplier<V> supplier) {
		return this.isOffThread() ? CompletableFuture.supplyAsync(supplier, this) : CompletableFuture.completedFuture(supplier.get());
	}

	public CompletableFuture<Object> executeFuture(Runnable runnable) {
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

	public void method_18858(R runnable) {
		this.taskQueue.add(runnable);
		LockSupport.unpark(this.method_3777());
	}

	public void execute(Runnable runnable) {
		if (this.isOffThread()) {
			this.method_18858(this.method_16211(runnable));
		} else {
			runnable.run();
		}
	}

	@Environment(EnvType.CLIENT)
	protected void method_18855() {
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
		} else if (this.field_18319 == 0 && !this.method_18856(runnable)) {
			return false;
		} else {
			this.method_18859((R)this.taskQueue.remove());
			return true;
		}
	}

	public void method_18857(BooleanSupplier booleanSupplier) {
		this.field_18319++;

		try {
			while (!booleanSupplier.getAsBoolean()) {
				if (!this.executeQueuedTask()) {
					LockSupport.parkNanos("waiting for tasks", 1000L);
				}
			}
		} finally {
			this.field_18319--;
		}
	}

	protected void method_18859(R runnable) {
		try {
			runnable.run();
		} catch (Exception var3) {
			LOGGER.fatal("Error executing task on {}", this.getName(), var3);
		}
	}
}
