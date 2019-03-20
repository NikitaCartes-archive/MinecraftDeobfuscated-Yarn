package net.minecraft.client.audio;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.ThreadTaskQueue;

@Environment(EnvType.CLIENT)
public class SoundTaskQueue extends ThreadTaskQueue<Runnable> {
	private Thread thread = this.createThread();
	private volatile boolean stopped;

	public SoundTaskQueue() {
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
	protected Runnable prepareRunnable(Runnable runnable) {
		return runnable;
	}

	@Override
	protected boolean canRun(Runnable runnable) {
		return !this.stopped;
	}

	@Override
	protected Thread getThread() {
		return this.thread;
	}

	private void waitForStop() {
		while (!this.stopped) {
			this.waitFor(() -> this.stopped);
		}
	}

	public void restart() {
		this.stopped = true;
		this.thread.interrupt();

		try {
			this.thread.join();
		} catch (InterruptedException var2) {
			Thread.currentThread().interrupt();
		}

		this.clear();
		this.stopped = false;
		this.thread = this.createThread();
	}
}
