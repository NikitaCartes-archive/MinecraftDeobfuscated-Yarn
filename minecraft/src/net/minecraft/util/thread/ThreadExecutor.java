package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import com.mojang.jtracy.TracyClient;
import com.mojang.jtracy.Zone;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.CheckReturnValue;
import net.minecraft.SharedConstants;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.profiler.SampleType;
import net.minecraft.util.profiler.Sampler;
import org.slf4j.Logger;

public abstract class ThreadExecutor<R extends Runnable> implements SampleableExecutor, TaskExecutor<R>, Executor {
	public static final long field_52421 = 100000L;
	private final String name;
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Queue<R> tasks = Queues.<R>newConcurrentLinkedQueue();
	private int executionsInProgress;

	protected ThreadExecutor(String name) {
		this.name = name;
		ExecutorSampling.INSTANCE.add(this);
	}

	protected abstract boolean canExecute(R task);

	public boolean isOnThread() {
		return Thread.currentThread() == this.getThread();
	}

	protected abstract Thread getThread();

	protected boolean shouldExecuteAsync() {
		return !this.isOnThread();
	}

	public int getTaskCount() {
		return this.tasks.size();
	}

	@Override
	public String getName() {
		return this.name;
	}

	public <V> CompletableFuture<V> submit(Supplier<V> task) {
		return this.shouldExecuteAsync() ? CompletableFuture.supplyAsync(task, this) : CompletableFuture.completedFuture(task.get());
	}

	private CompletableFuture<Void> submitAsync(Runnable runnable) {
		return CompletableFuture.supplyAsync(() -> {
			runnable.run();
			return null;
		}, this);
	}

	@CheckReturnValue
	public CompletableFuture<Void> submit(Runnable task) {
		if (this.shouldExecuteAsync()) {
			return this.submitAsync(task);
		} else {
			task.run();
			return CompletableFuture.completedFuture(null);
		}
	}

	public void submitAndJoin(Runnable runnable) {
		if (!this.isOnThread()) {
			this.submitAsync(runnable).join();
		} else {
			runnable.run();
		}
	}

	@Override
	public void send(R runnable) {
		this.tasks.add(runnable);
		LockSupport.unpark(this.getThread());
	}

	public void execute(Runnable runnable) {
		if (this.shouldExecuteAsync()) {
			this.send(this.createTask(runnable));
		} else {
			runnable.run();
		}
	}

	public void executeSync(Runnable runnable) {
		this.execute(runnable);
	}

	protected void cancelTasks() {
		this.tasks.clear();
	}

	protected void runTasks() {
		while (this.runTask()) {
		}
	}

	public boolean runTask() {
		R runnable = (R)this.tasks.peek();
		if (runnable == null) {
			return false;
		} else if (this.executionsInProgress == 0 && !this.canExecute(runnable)) {
			return false;
		} else {
			this.executeTask((R)this.tasks.remove());
			return true;
		}
	}

	public void runTasks(BooleanSupplier stopCondition) {
		this.executionsInProgress++;

		try {
			while (!stopCondition.getAsBoolean()) {
				if (!this.runTask()) {
					this.waitForTasks();
				}
			}
		} finally {
			this.executionsInProgress--;
		}
	}

	protected void waitForTasks() {
		Thread.yield();
		LockSupport.parkNanos("waiting for tasks", 100000L);
	}

	protected void executeTask(R task) {
		try (Zone zone = TracyClient.beginZone("Task", SharedConstants.isDevelopment)) {
			task.run();
		} catch (Exception var7) {
			LOGGER.error(LogUtils.FATAL_MARKER, "Error executing task on {}", this.getName(), var7);
			if (isMemoryError(var7)) {
				throw var7;
			}
		}
	}

	@Override
	public List<Sampler> createSamplers() {
		return ImmutableList.of(Sampler.create(this.name + "-pending-tasks", SampleType.EVENT_LOOPS, this::getTaskCount));
	}

	/**
	 * {@return whether {@code exception} represents an {@link OutOfMemoryError} or
	 * {@link StackOverflowError}}
	 */
	public static boolean isMemoryError(Throwable exception) {
		return exception instanceof CrashException crashException
			? isMemoryError(crashException.getCause())
			: exception instanceof OutOfMemoryError || exception instanceof StackOverflowError;
	}
}
