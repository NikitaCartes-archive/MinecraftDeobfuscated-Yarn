/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.Nullable;

/**
 * A runner for tasks that can hold one pending task.
 * 
 * <p>To queue a task for running, call {@link #queue}, and to run the task,
 * call {@link #runPending}.
 */
public class PendingTaskRunner {
    private final AtomicReference<FutureRunnable> reference = new AtomicReference();
    @Nullable
    private CompletableFuture<?> future;

    /**
     * Runs the pending task, if any, and marks the runner as not running.
     */
    public void runPending() {
        if (this.future != null && this.future.isDone()) {
            this.future = null;
        }
        if (this.future == null) {
            this.runPendingInternal();
        }
    }

    private void runPendingInternal() {
        FutureRunnable futureRunnable = this.reference.getAndSet(null);
        if (futureRunnable != null) {
            this.future = futureRunnable.run();
        }
    }

    public void queue(FutureRunnable task) {
        this.reference.set(task);
    }

    @FunctionalInterface
    public static interface FutureRunnable {
        public CompletableFuture<?> run();
    }
}

