/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.thread;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.slf4j.Logger;

/**
 * A functional interface that can enqueue completable futures.
 */
@FunctionalInterface
public interface FutureQueue {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final FutureQueue NOOP = future -> ((CompletableFuture)future.get()).exceptionally(throwable -> {
        LOGGER.error("Task failed", (Throwable)throwable);
        return null;
    });

    public void append(FutureSupplier var1);

    public static interface FutureSupplier
    extends Supplier<CompletableFuture<?>> {
    }
}

