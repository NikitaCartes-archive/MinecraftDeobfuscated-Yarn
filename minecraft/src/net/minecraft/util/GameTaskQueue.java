package net.minecraft.util;

public abstract class GameTaskQueue<R extends Runnable> extends ThreadTaskQueue<R> {
	private int field_18320 = 0;

	public GameTaskQueue(String string) {
		super(string);
	}

	@Override
	protected boolean isOffThread() {
		return this.method_18860() || super.isOffThread();
	}

	protected boolean method_18860() {
		return this.field_18320 != 0;
	}

	@Override
	protected void runSafely(R runnable) {
		this.field_18320++;

		try {
			super.runSafely(runnable);
		} finally {
			this.field_18320--;
		}
	}
}
