package net.minecraft;

import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_1255<R extends Runnable> implements class_3906<R>, Executor {
	private final String field_18318;
	private static final Logger field_5751 = LogManager.getLogger();
	private final Queue<R> field_5750 = Queues.<R>newConcurrentLinkedQueue();
	private int field_18319;

	protected class_1255(String string) {
		this.field_18318 = string;
	}

	protected abstract R method_16211(Runnable runnable);

	protected abstract boolean method_18856(R runnable);

	public boolean method_18854() {
		return Thread.currentThread() == this.method_3777();
	}

	protected abstract Thread method_3777();

	protected boolean method_5384() {
		return !this.method_18854();
	}

	@Override
	public String method_16898() {
		return this.field_18318;
	}

	@Environment(EnvType.CLIENT)
	public <V> CompletableFuture<V> method_5385(Supplier<V> supplier) {
		return this.method_5384() ? CompletableFuture.supplyAsync(supplier, this) : CompletableFuture.completedFuture(supplier.get());
	}

	private CompletableFuture<Object> method_5382(Runnable runnable) {
		return CompletableFuture.supplyAsync(() -> {
			runnable.run();
			return null;
		}, this);
	}

	@Environment(EnvType.CLIENT)
	public CompletableFuture<Object> method_20231(Runnable runnable) {
		if (this.method_5384()) {
			return this.method_5382(runnable);
		} else {
			runnable.run();
			return CompletableFuture.completedFuture(null);
		}
	}

	public void method_19537(Runnable runnable) {
		if (!this.method_18854()) {
			this.method_5382(runnable).join();
		} else {
			runnable.run();
		}
	}

	public void method_18858(R runnable) {
		this.field_5750.add(runnable);
		LockSupport.unpark(this.method_3777());
	}

	public void execute(Runnable runnable) {
		if (this.method_5384()) {
			this.method_18858(this.method_16211(runnable));
		} else {
			runnable.run();
		}
	}

	@Environment(EnvType.CLIENT)
	protected void method_18855() {
		this.field_5750.clear();
	}

	protected void method_5383() {
		while (this.method_16075()) {
		}
	}

	protected boolean method_16075() {
		R runnable = (R)this.field_5750.peek();
		if (runnable == null) {
			return false;
		} else if (this.field_18319 == 0 && !this.method_18856(runnable)) {
			return false;
		} else {
			this.method_18859((R)this.field_5750.remove());
			return true;
		}
	}

	public void method_18857(BooleanSupplier booleanSupplier) {
		this.field_18319++;

		try {
			while (!booleanSupplier.getAsBoolean()) {
				if (!this.method_16075()) {
					LockSupport.parkNanos("waiting for tasks", 1000L);
				}
			}
		} finally {
			this.field_18319--;
		}
	}

	protected void method_18859(R runnable) {
		try {
			runnable.run();
		} catch (Exception var3) {
			field_5751.fatal("Error executing task on {}", this.method_16898(), var3);
		}
	}
}
