package net.minecraft;

public abstract class class_4093<R extends Runnable> extends class_1255<R> {
	private int field_18320;

	public class_4093(String string) {
		super(string);
	}

	@Override
	protected boolean method_5384() {
		return this.method_18860() || super.method_5384();
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
