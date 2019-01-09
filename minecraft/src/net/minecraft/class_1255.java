package net.minecraft;

import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_1255<R extends Runnable> implements Executor {
	private static final Logger field_5751 = LogManager.getLogger();
	protected final Queue<R> field_5750 = Queues.<R>newConcurrentLinkedQueue();

	@Environment(EnvType.CLIENT)
	public <V> CompletableFuture<V> method_5385(Supplier<V> supplier) {
		Validate.notNull(supplier);
		return this.method_5384() ? CompletableFuture.supplyAsync(supplier, this) : CompletableFuture.completedFuture(supplier.get());
	}

	public CompletableFuture<Object> method_5382(Runnable runnable) {
		Validate.notNull(runnable);
		if (this.method_5384()) {
			return CompletableFuture.supplyAsync(() -> {
				runnable.run();
				return null;
			}, this);
		} else {
			runnable.run();
			return CompletableFuture.completedFuture(null);
		}
	}

	public void execute(Runnable runnable) {
		if (this.method_5384()) {
			this.field_5750.add(this.method_16211(runnable));
		} else {
			runnable.run();
		}
	}

	protected abstract R method_16211(Runnable runnable);

	@Environment(EnvType.CLIENT)
	protected void method_5383() {
		while (this.method_16075()) {
		}
	}

	protected boolean method_16075() {
		R runnable = (R)this.field_5750.poll();
		if (runnable == null) {
			return false;
		} else {
			try {
				runnable.run();
			} catch (Exception var3) {
				field_5751.fatal("Error executing task", (Throwable)var3);
			}

			return true;
		}
	}

	public boolean method_5384() {
		return !this.method_5387();
	}

	public abstract boolean method_5387();
}
