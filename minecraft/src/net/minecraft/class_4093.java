package net.minecraft;

import net.minecraft.util.ThreadTaskQueue;

public abstract class class_4093<R extends Runnable> extends ThreadTaskQueue<R> {
	private int field_18320 = 0;

	public class_4093(String string) {
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
	protected void method_18859(R runnable) {
		this.field_18320++;

		try {
			super.method_18859(runnable);
		} finally {
			this.field_18320--;
		}
	}
}
