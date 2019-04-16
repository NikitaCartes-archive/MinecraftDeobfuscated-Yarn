package net.minecraft.util;

public abstract class NonBlockingThreadExecutor<R extends Runnable> extends ThreadExecutor<R> {
	private int runningTasks;

	public NonBlockingThreadExecutor(String string) {
		super(string);
	}

	@Override
	protected boolean shouldRunAsync() {
		return this.hasRunningTasks() || super.shouldRunAsync();
	}

	protected boolean hasRunningTasks() {
		return this.runningTasks != 0;
	}

	@Override
	protected void runSafely(R runnable) {
		this.runningTasks++;

		try {
			super.runSafely(runnable);
		} finally {
			this.runningTasks--;
		}
	}
}
