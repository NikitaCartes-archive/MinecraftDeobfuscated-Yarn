package net.minecraft;

import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3846<T> implements AutoCloseable, Runnable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final AtomicInteger field_17041 = new AtomicInteger(0);
	public final class_3847<T, ? extends Runnable> field_17039;
	private final Executor field_17042;
	private final String field_17043;

	public static class_3846<Runnable> method_16902(Executor executor, String string) {
		return new class_3846<>(new class_3847.class_3849<>(new ConcurrentLinkedQueue()), executor, string);
	}

	public class_3846(class_3847<T, ? extends Runnable> arg, Executor executor, String string) {
		this.field_17042 = executor;
		this.field_17039 = arg;
		this.field_17043 = string;
	}

	private boolean method_16903() {
		int i;
		do {
			i = this.field_17041.get();
			if ((i & 3) != 0) {
				return false;
			}
		} while (!this.field_17041.compareAndSet(i, i | 2));

		return true;
	}

	private void method_16904() {
		int i;
		do {
			i = this.field_17041.get();
		} while (!this.field_17041.compareAndSet(i, i & -3));
	}

	private boolean method_16905() {
		return (this.field_17041.get() & 1) != 0 ? false : !this.field_17039.method_16911();
	}

	public void close() {
		int i;
		do {
			i = this.field_17041.get();
		} while (!this.field_17041.compareAndSet(i, i | 1));
	}

	private boolean method_16906() {
		return (this.field_17041.get() & 2) != 0;
	}

	private boolean method_16907() {
		if (!this.method_16906()) {
			return false;
		} else {
			Runnable runnable = this.field_17039.method_16909();
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
			this.method_16900(i -> i == 0);
		} finally {
			this.method_16904();
			this.method_16908();
		}
	}

	public void method_16901(T object) {
		this.field_17039.method_16910(object);
		this.method_16908();
	}

	private void method_16908() {
		if (this.method_16905() && this.method_16903()) {
			try {
				this.field_17042.execute(this);
			} catch (RejectedExecutionException var4) {
				try {
					this.field_17042.execute(this);
				} catch (RejectedExecutionException var3) {
					LOGGER.error("Cound not schedule mailbox", (Throwable)var3);
				}
			}
		}
	}

	private int method_16900(Int2BooleanFunction int2BooleanFunction) {
		int i = 0;

		while (int2BooleanFunction.get(i) && this.method_16907()) {
			i++;
		}

		return i;
	}

	public String toString() {
		return this.field_17043 + " " + this.field_17041.get() + " " + this.field_17039.method_16911();
	}

	public String method_16898() {
		return this.field_17043;
	}
}
