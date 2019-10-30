package net.minecraft.client.sound;

import java.util.concurrent.locks.LockSupport;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.thread.ThreadExecutor;

@Environment(EnvType.CLIENT)
public class SoundExecutor extends ThreadExecutor<Runnable> {
	private Thread thread = this.createThread();
	private volatile boolean stopped;

	public SoundExecutor() {
		super("Sound executor");
	}

	private Thread createThread() {
		Thread thread = new Thread(this::waitForStop);
		thread.setDaemon(true);
		thread.setName("Sound engine");
		thread.start();
		return thread;
	}

	@Override
	protected Runnable createTask(Runnable runnable) {
		return runnable;
	}

	@Override
	protected boolean canExecute(Runnable task) {
		return !this.stopped;
	}

	@Override
	protected Thread getThread() {
		return this.thread;
	}

	private void waitForStop() {
		while (!this.stopped) {
			this.runTasks(() -> this.stopped);
		}
	}

	@Override
	protected void waitForTasks() {
		LockSupport.park("waiting for tasks");
	}

	public void restart() {
		this.stopped = true;
		this.thread.interrupt();

		try {
			this.thread.join();
		} catch (InterruptedException var2) {
			Thread.currentThread().interrupt();
		}

		this.cancelTasks();
		this.stopped = false;
		this.thread = this.createThread();
	}
}
