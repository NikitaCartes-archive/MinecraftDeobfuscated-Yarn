package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.util.profiler.Metric;
import net.minecraft.client.util.profiler.MetricSampler;
import net.minecraft.client.util.profiler.SamplingChannel;
import net.minecraft.util.profiler.MetricSamplerSupplier;
import net.minecraft.util.profiler.MetricSuppliers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ThreadExecutor<R extends Runnable> implements MetricSamplerSupplier, MessageListener<R>, Executor {
	private final String name;
	private static final Logger LOGGER = LogManager.getLogger();
	private final Queue<R> tasks = Queues.<R>newConcurrentLinkedQueue();
	private int executionsInProgress;

	protected ThreadExecutor(String name) {
		this.name = name;
		MetricSuppliers.INSTANCE.add(this);
	}

	protected abstract R createTask(Runnable runnable);

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

	protected void cancelTasks() {
		this.tasks.clear();
	}

	protected void runTasks() {
		while (this.runTask()) {
		}
	}

	protected boolean runTask() {
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
		try {
			task.run();
		} catch (Exception var3) {
			LOGGER.fatal("Error executing task on {}", this.getName(), var3);
		}
	}

	@Override
	public List<MetricSampler> getSamplers() {
		return ImmutableList.of(new MetricSampler(new Metric(this.name + "-tasks-pending"), this::getTaskCount, SamplingChannel.EVENT_LOOP));
	}
}
