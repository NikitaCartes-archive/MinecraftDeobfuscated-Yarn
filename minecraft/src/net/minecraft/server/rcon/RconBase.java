package net.minecraft.server.rcon;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.logging.UncaughtExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RconBase implements Runnable {
	private static final Logger field_14430 = LogManager.getLogger();
	private static final AtomicInteger field_14428 = new AtomicInteger(0);
	protected volatile boolean running;
	protected final String description;
	protected Thread thread;

	protected RconBase(String string) {
		this.description = string;
	}

	public synchronized void start() {
		this.running = true;
		this.thread = new Thread(this, this.description + " #" + field_14428.incrementAndGet());
		this.thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler(field_14430));
		this.thread.start();
		field_14430.info("Thread {} started", this.description);
	}

	public synchronized void stop() {
		this.running = false;
		if (null != this.thread) {
			int i = 0;

			while (this.thread.isAlive()) {
				try {
					this.thread.join(1000L);
					if (++i >= 5) {
						field_14430.warn("Waited {} seconds attempting force stop!", i);
					} else if (this.thread.isAlive()) {
						field_14430.warn("Thread {} ({}) failed to exit after {} second(s)", this, this.thread.getState(), i, new Exception("Stack:"));
						this.thread.interrupt();
					}
				} catch (InterruptedException var3) {
				}
			}

			field_14430.info("Thread {} stopped", this.description);
			this.thread = null;
		}
	}

	public boolean isRunning() {
		return this.running;
	}
}
