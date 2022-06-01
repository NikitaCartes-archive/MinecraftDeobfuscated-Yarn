/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.Nullable;

/**
 * A runner for tasks that can hold only one pending task. It replaces the
 * current pending task if a new one is queued, but does not halt already
 * running tasks.
 * 
 * <p>The runner needs to be updated by {@link #tick}, which checks if
 * the running task is done and polls the pending task. {@link #queue}
 * sets the pending task for running.
 */
public class PendingTaskRunner {
    private final AtomicReference<FutureRunnable> pending = new AtomicReference();
    @Nullable
    private CompletableFuture<?> running;

    /**
     * Waits for the running task to finish and polls the pending task if there
     * is no running task.
     */
    public void tick() {
        if (this.running != null && this.running.isDone()) {
            this.running = null;
        }
        if (this.running == null) {
            this.poll();
        }
    }

    private void poll() {
        FutureRunnable futureRunnable = this.pending.getAndSet(null);
        if (futureRunnable != null) {
            this.running = futureRunnable.run();
        }
    }

    /**
     * Queues a task for running, and replaces any existing pending task.
     * 
     * @apiNote This method can be called asynchronously, such as from the
     * netty event loop.
     */
    public void queue(FutureRunnable task) {
        this.pending.set(task);
    }

    @FunctionalInterface
    public static interface FutureRunnable {
        public CompletableFuture<?> run();
    }
}

