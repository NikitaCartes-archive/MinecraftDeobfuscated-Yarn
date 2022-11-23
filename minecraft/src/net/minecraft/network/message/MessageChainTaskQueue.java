package net.minecraft.network.message;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.util.thread.FutureQueue;
import org.slf4j.Logger;

/**
 * Queues a future that handles received messages on the server thread.
 */
public class MessageChainTaskQueue implements FutureQueue, AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private CompletableFuture<?> current = CompletableFuture.completedFuture(null);
	private final Executor executor;
	private volatile boolean closed;

	public MessageChainTaskQueue(Executor executor) {
		this.executor = runnable -> {
			if (!this.closed) {
				executor.execute(runnable);
			}
		};
	}

	@Override
	public void append(FutureQueue.FutureSupplier futureSupplier) {
		this.current = this.current.thenComposeAsync(object -> futureSupplier.submit(this.executor), this.executor).exceptionally(throwable -> {
			if (throwable instanceof CompletionException completionException) {
				throwable = completionException.getCause();
			}

			if (throwable instanceof CancellationException cancellationException) {
				throw cancellationException;
			} else {
				LOGGER.error("Chain link failed, continuing to next one", throwable);
				return null;
			}
		});
	}

	public void close() {
		this.closed = true;
	}
}
