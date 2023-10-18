package net.minecraft.network.message;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
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
		this.executor = executor;
	}

	@Override
	public <T> void append(CompletableFuture<T> completableFuture, Consumer<T> consumer) {
		this.current = this.current.thenCombine(completableFuture, (a, b) -> b).thenAcceptAsync(object -> {
			if (!this.closed) {
				consumer.accept(object);
			}
		}, this.executor).exceptionally(throwable -> {
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
