package net.minecraft.util.thread;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.slf4j.Logger;

/**
 * A functional interface that can enqueue completable futures.
 */
@FunctionalInterface
public interface FutureQueue {
	Logger LOGGER = LogUtils.getLogger();

	static FutureQueue immediate(Executor executor) {
		return future -> future.submit(executor).exceptionally(throwable -> {
				LOGGER.error("Task failed", throwable);
				return null;
			});
	}

	void append(FutureQueue.FutureSupplier future);

	/**
	 * A functional interface supplying the queued future to {@link FutureQueue}.
	 */
	public interface FutureSupplier {
		CompletableFuture<?> submit(Executor executor);
	}
}
