package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4238 extends class_1255<Runnable> {
	private Thread field_18953 = this.method_19764();
	private volatile boolean field_18954;

	public class_4238() {
		super("Sound executor");
	}

	private Thread method_19764() {
		Thread thread = new Thread(this::method_19765);
		thread.setDaemon(true);
		thread.setName("Sound engine");
		thread.start();
		return thread;
	}

	@Override
	protected Runnable method_16211(Runnable runnable) {
		return runnable;
	}

	@Override
	protected boolean method_18856(Runnable runnable) {
		return !this.field_18954;
	}

	@Override
	protected Thread method_3777() {
		return this.field_18953;
	}

	private void method_19765() {
		while (!this.field_18954) {
			this.method_18857(() -> this.field_18954);
		}
	}

	public void method_19763() {
		this.field_18954 = true;
		this.field_18953.interrupt();

		try {
			this.field_18953.join();
		} catch (InterruptedException var2) {
			Thread.currentThread().interrupt();
		}

		this.method_18855();
		this.field_18954 = false;
		this.field_18953 = this.method_19764();
	}
}
