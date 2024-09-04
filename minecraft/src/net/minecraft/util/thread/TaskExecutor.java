package net.minecraft.util.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public interface TaskExecutor<R extends Runnable> extends AutoCloseable {
	String getName();

	void send(R runnable);

	default void close() {
	}

	R createTask(Runnable runnable);

	default <Source> CompletableFuture<Source> executeAsync(Consumer<CompletableFuture<Source>> future) {
		CompletableFuture<Source> completableFuture = new CompletableFuture();
		this.send(this.createTask(() -> future.accept(completableFuture)));
		return completableFuture;
	}

	static TaskExecutor<Runnable> of(String name, Executor executor) {
		return new TaskExecutor<Runnable>() {
			@Override
			public String getName() {
				return name;
			}

			@Override
			public void send(Runnable runnable) {
				executor.execute(runnable);
			}

			@Override
			public Runnable createTask(Runnable runnable) {
				return runnable;
			}

			public String toString() {
				return name;
			}
		};
	}
}
