package net.minecraft.util.thread;

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

public abstract class ThreadExecutor<R extends Runnable> implements MessageListener<R>, Executor {
	private final String name;
	private static final Logger LOGGER = LogManager.getLogger();
	private final Queue<R> tasks = Queues.<R>newConcurrentLinkedQueue();
	private int executionsInProgress;

	protected ThreadExecutor(String string) {
		this.name = string;
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

	public int method_21684() {
		return this.tasks.size();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Environment(EnvType.CLIENT)
	public <V> CompletableFuture<V> submit(Supplier<V> task) {
		return this.shouldExecuteAsync() ? CompletableFuture.supplyAsync(task, this) : CompletableFuture.completedFuture(task.get());
	}

	private CompletableFuture<Void> submitAsync(Runnable runnable) {
		return CompletableFuture.supplyAsync(() -> {
			runnable.run();
			return null;
		}, this);
	}

	public CompletableFuture<Void> method_20493(Runnable runnable) {
		if (this.shouldExecuteAsync()) {
			return this.submitAsync(runnable);
		} else {
			runnable.run();
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

	@Environment(EnvType.CLIENT)
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
					this.method_20813();
				}
			}
		} finally {
			this.executionsInProgress--;
		}
	}

	protected void method_20813() {
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
}
