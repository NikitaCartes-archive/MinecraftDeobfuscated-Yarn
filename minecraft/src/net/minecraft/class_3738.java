package net.minecraft;

public class class_3738 implements Runnable {
	private final int field_16504;
	private final Runnable field_16505;

	public class_3738(int i, Runnable runnable) {
		this.field_16504 = i;
		this.field_16505 = runnable;
	}

	public int method_16338() {
		return this.field_16504;
	}

	public void run() {
		this.field_16505.run();
	}
}
