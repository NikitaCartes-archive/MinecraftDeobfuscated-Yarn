package net.minecraft.util;

import javax.annotation.Nullable;

/**
 * A runner for tasks that have a running state and can hold one pending task.
 * 
 * <p>If there is no running tasks, calling {@link #run} will run the passed task
 * and marks the runner as running. During this state, calling the run method will set
 * the task as pending. If called multiple times, only the last task will be called.
 * Calling {@link #runPending} forces it to run the pending task, if any.
 */
public class PendingTaskRunner {
	private boolean running;
	@Nullable
	private Runnable pending;

	/**
	 * Runs the pending task, if any, and marks the runner as not running.
	 */
	public void runPending() {
		Runnable runnable = this.pending;
		if (runnable != null) {
			runnable.run();
			this.pending = null;
		}

		this.running = false;
	}

	/**
	 * Runs the task and marks the runner as running if there is no running task,
	 * otherwise sets the task as the pending task. This overwrites the old pending task.
	 */
	public void run(Runnable task) {
		if (this.running) {
			this.pending = task;
		} else {
			task.run();
			this.running = true;
			this.pending = null;
		}
	}
}
