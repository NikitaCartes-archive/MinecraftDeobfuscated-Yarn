package net.minecraft.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;

/**
 * A runner for tasks that can hold one pending task.
 * 
 * <p>To queue a task for running, call {@link #queue}, and to run the task,
 * call {@link #runPending}.
 */
public class PendingTaskRunner {
	private final AtomicReference<PendingTaskRunner.FutureRunnable> reference = new AtomicReference();
	@Nullable
	private CompletableFuture<?> future;

	/**
	 * Runs the pending task, if any, and marks the runner as not running.
	 */
	public void runPending() {
		if (this.future != null && this.future.isDone()) {
			this.future = null;
		}

		if (this.future == null) {
			this.runPendingInternal();
		}
	}

	private void runPendingInternal() {
		PendingTaskRunner.FutureRunnable futureRunnable = (PendingTaskRunner.FutureRunnable)this.reference.getAndSet(null);
		if (futureRunnable != null) {
			this.future = futureRunnable.run();
		}
	}

	public void queue(PendingTaskRunner.FutureRunnable task) {
		this.reference.set(task);
	}

	@FunctionalInterface
	public interface FutureRunnable {
		CompletableFuture<?> run();
	}
}
