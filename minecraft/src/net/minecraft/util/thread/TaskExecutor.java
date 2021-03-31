package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.util.profiler.Metric;
import net.minecraft.client.util.profiler.MetricSampler;
import net.minecraft.client.util.profiler.SamplingChannel;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.MetricSamplerSupplier;
import net.minecraft.util.profiler.MetricSuppliers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TaskExecutor<T> implements MetricSamplerSupplier, MessageListener<T>, AutoCloseable, Runnable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int field_29940 = 1;
	private static final int field_29941 = 2;
	private final AtomicInteger stateFlags = new AtomicInteger(0);
	private final TaskQueue<? super T, ? extends Runnable> queue;
	private final Executor executor;
	private final String name;

	public static TaskExecutor<Runnable> create(Executor executor, String name) {
		return new TaskExecutor<>(new TaskQueue.Simple<>(new ConcurrentLinkedQueue()), executor, name);
	}

	public TaskExecutor(TaskQueue<? super T, ? extends Runnable> queue, Executor executor, String name) {
		this.executor = executor;
		this.queue = queue;
		this.name = name;
		MetricSuppliers.INSTANCE.add(this);
	}

	private boolean unpause() {
		int i;
		do {
			i = this.stateFlags.get();
			if ((i & 3) != 0) {
				return false;
			}
		} while (!this.stateFlags.compareAndSet(i, i | 2));

		return true;
	}

	private void pause() {
		int i;
		do {
			i = this.stateFlags.get();
		} while (!this.stateFlags.compareAndSet(i, i & -3));
	}

	private boolean hasMessages() {
		return (this.stateFlags.get() & 1) != 0 ? false : !this.queue.isEmpty();
	}

	@Override
	public void close() {
		int i;
		do {
			i = this.stateFlags.get();
		} while (!this.stateFlags.compareAndSet(i, i | 1));
	}

	private boolean isUnpaused() {
		return (this.stateFlags.get() & 2) != 0;
	}

	private boolean runNext() {
		if (!this.isUnpaused()) {
			return false;
		} else {
			Runnable runnable = this.queue.poll();
			if (runnable == null) {
				return false;
			} else {
				Util.debugRunnable(this.name, runnable).run();
				return true;
			}
		}
	}

	public void run() {
		try {
			this.runWhile(i -> i == 0);
		} finally {
			this.pause();
			this.execute();
		}
	}

	@Override
	public void send(T message) {
		this.queue.add(message);
		this.execute();
	}

	private void execute() {
		if (this.hasMessages() && this.unpause()) {
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

	private int runWhile(Int2BooleanFunction condition) {
		int i = 0;

		while (condition.get(i) && this.runNext()) {
			i++;
		}

		return i;
	}

	public int method_34996() {
		return this.queue.getSize();
	}

	public String toString() {
		return this.name + " " + this.stateFlags.get() + " " + this.queue.isEmpty();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public List<MetricSampler> getSamplers() {
		return ImmutableList.of(new MetricSampler(new Metric(this.name + "-queuesize"), this.queue::getSize, SamplingChannel.MAIL_BOX));
	}
}
