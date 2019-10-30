package net.minecraft.util.thread;

public abstract class ReentrantThreadExecutor<R extends Runnable> extends ThreadExecutor<R> {
	private int runningTasks;

	public ReentrantThreadExecutor(String name) {
		super(name);
	}

	@Override
	protected boolean shouldExecuteAsync() {
		return this.hasRunningTasks() || super.shouldExecuteAsync();
	}

	protected boolean hasRunningTasks() {
		return this.runningTasks != 0;
	}

	@Override
	protected void executeTask(R task) {
		this.runningTasks++;

		try {
			super.executeTask(task);
		} finally {
			this.runningTasks--;
		}
	}
}
