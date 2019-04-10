package net.minecraft.util;

import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MailboxProcessor<T> implements Actor<T>, AutoCloseable, Runnable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final AtomicInteger stateFlags = new AtomicInteger(0);
	public final Mailbox<? super T, ? extends Runnable> mailbox;
	private final Executor executor;
	private final String name;

	public static MailboxProcessor<Runnable> create(Executor executor, String string) {
		return new MailboxProcessor<>(new Mailbox.QueueMailbox<>(new ConcurrentLinkedQueue()), executor, string);
	}

	public MailboxProcessor(Mailbox<? super T, ? extends Runnable> mailbox, Executor executor, String string) {
		this.executor = executor;
		this.mailbox = mailbox;
		this.name = string;
	}

	private boolean lock() {
		int i;
		do {
			i = this.stateFlags.get();
			if ((i & 3) != 0) {
				return false;
			}
		} while (!this.stateFlags.compareAndSet(i, i | 2));

		return true;
	}

	private void unlock() {
		int i;
		do {
			i = this.stateFlags.get();
		} while (!this.stateFlags.compareAndSet(i, i & -3));
	}

	private boolean hasMessages() {
		return (this.stateFlags.get() & 1) != 0 ? false : !this.mailbox.isEmpty();
	}

	@Override
	public void close() {
		int i;
		do {
			i = this.stateFlags.get();
		} while (!this.stateFlags.compareAndSet(i, i | 1));
	}

	private boolean isLocked() {
		return (this.stateFlags.get() & 2) != 0;
	}

	private boolean runNext() {
		if (!this.isLocked()) {
			return false;
		} else {
			Runnable runnable = this.mailbox.poll();
			if (runnable == null) {
				return false;
			} else {
				runnable.run();
				return true;
			}
		}
	}

	public void run() {
		try {
			this.run(i -> i == 0);
		} finally {
			this.unlock();
			this.execute();
		}
	}

	@Override
	public void send(T object) {
		this.mailbox.add(object);
		this.execute();
	}

	private void execute() {
		if (this.hasMessages() && this.lock()) {
			try {
				this.executor.execute(this);
			} catch (RejectedExecutionException var4) {
				try {
					this.executor.execute(this);
				} catch (RejectedExecutionException var3) {
					LOGGER.error("Cound not schedule mailbox", (Throwable)var3);
				}
			}
		}
	}

	private int run(Int2BooleanFunction int2BooleanFunction) {
		int i = 0;

		while (int2BooleanFunction.get(i) && this.runNext()) {
			i++;
		}

		return i;
	}

	public String toString() {
		return this.name + " " + this.stateFlags.get() + " " + this.mailbox.isEmpty();
	}

	@Override
	public String getName() {
		return this.name;
	}
}
