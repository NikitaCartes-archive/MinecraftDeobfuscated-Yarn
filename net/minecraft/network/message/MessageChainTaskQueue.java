/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
public class MessageChainTaskQueue
implements FutureQueue,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private CompletableFuture<?> current = CompletableFuture.completedFuture(null);
    private final Executor executor = runnable -> {
        if (!this.closed) {
            executor.execute(runnable);
        }
    };
    private volatile boolean closed;

    public MessageChainTaskQueue(Executor executor) {
    }

    @Override
    public void append(FutureQueue.FutureSupplier futureSupplier) {
        this.current = ((CompletableFuture)this.current.thenComposeAsync(object -> futureSupplier.submit(this.executor), this.executor)).exceptionally(throwable -> {
            if (throwable instanceof CompletionException) {
                CompletionException completionException = (CompletionException)throwable;
                throwable = completionException.getCause();
            }
            if (throwable instanceof CancellationException) {
                CancellationException cancellationException = (CancellationException)throwable;
                throw cancellationException;
            }
            LOGGER.error("Chain link failed, continuing to next one", (Throwable)throwable);
            return null;
        });
    }

    @Override
    public void close() {
        this.closed = true;
    }
}

