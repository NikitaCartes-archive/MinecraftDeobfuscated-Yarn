/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.TaskQueue;
import net.minecraft.world.storage.RegionBasedStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class StorageIoWorker
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final AtomicBoolean closed = new AtomicBoolean();
    private final TaskExecutor<TaskQueue.PrioritizedTask> executor;
    private final RegionBasedStorage storage;
    private final Map<ChunkPos, Result> results = Maps.newLinkedHashMap();

    protected StorageIoWorker(File directory, boolean dsync, String name) {
        this.storage = new RegionBasedStorage(directory, dsync);
        this.executor = new TaskExecutor<TaskQueue.PrioritizedTask>(new TaskQueue.Prioritized(Priority.values().length), Util.getIoWorkerExecutor(), "IOWorker-" + name);
    }

    public CompletableFuture<Void> setResult(ChunkPos pos, @Nullable NbtCompound nbt) {
        return this.run(() -> {
            Result result = this.results.computeIfAbsent(pos, chunkPos -> new Result(nbt));
            result.nbt = nbt;
            return Either.left(result.future);
        }).thenCompose(Function.identity());
    }

    @Nullable
    public NbtCompound getNbt(ChunkPos pos) throws IOException {
        CompletableFuture<NbtCompound> completableFuture = this.readChunkData(pos);
        try {
            return completableFuture.join();
        } catch (CompletionException completionException) {
            if (completionException.getCause() instanceof IOException) {
                throw (IOException)completionException.getCause();
            }
            throw completionException;
        }
    }

    protected CompletableFuture<NbtCompound> readChunkData(ChunkPos pos) {
        return this.run(() -> {
            Result result = this.results.get(pos);
            if (result != null) {
                return Either.left(result.nbt);
            }
            try {
                NbtCompound nbtCompound = this.storage.getTagAt(pos);
                return Either.left(nbtCompound);
            } catch (Exception exception) {
                LOGGER.warn("Failed to read chunk {}", (Object)pos, (Object)exception);
                return Either.right(exception);
            }
        });
    }

    public CompletableFuture<Void> completeAll(boolean sync) {
        CompletionStage completableFuture = this.run(() -> Either.left(CompletableFuture.allOf((CompletableFuture[])this.results.values().stream().map(result -> result.future).toArray(CompletableFuture[]::new)))).thenCompose(Function.identity());
        if (sync) {
            return ((CompletableFuture)completableFuture).thenCompose(void_ -> this.run(() -> {
                try {
                    this.storage.sync();
                    return Either.left(null);
                } catch (Exception exception) {
                    LOGGER.warn("Failed to synchronize chunks", (Throwable)exception);
                    return Either.right(exception);
                }
            }));
        }
        return ((CompletableFuture)completableFuture).thenCompose(void_ -> this.run(() -> Either.left(null)));
    }

    private <T> CompletableFuture<T> run(Supplier<Either<T, Exception>> task) {
        return this.executor.askFallible(messageListener -> new TaskQueue.PrioritizedTask(Priority.FOREGROUND.ordinal(), () -> this.method_27939(messageListener, (Supplier)task)));
    }

    private void writeResult() {
        if (this.results.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<ChunkPos, Result>> iterator = this.results.entrySet().iterator();
        Map.Entry<ChunkPos, Result> entry = iterator.next();
        iterator.remove();
        this.write(entry.getKey(), entry.getValue());
        this.writeRemainingResults();
    }

    private void writeRemainingResults() {
        this.executor.send(new TaskQueue.PrioritizedTask(Priority.BACKGROUND.ordinal(), this::writeResult));
    }

    private void write(ChunkPos pos, Result result) {
        try {
            this.storage.write(pos, result.nbt);
            result.future.complete(null);
        } catch (Exception exception) {
            LOGGER.error("Failed to store chunk {}", (Object)pos, (Object)exception);
            result.future.completeExceptionally(exception);
        }
    }

    @Override
    public void close() throws IOException {
        if (!this.closed.compareAndSet(false, true)) {
            return;
        }
        this.executor.ask(messageListener -> new TaskQueue.PrioritizedTask(Priority.SHUTDOWN.ordinal(), () -> messageListener.send(Unit.INSTANCE))).join();
        this.executor.close();
        try {
            this.storage.close();
        } catch (Exception exception) {
            LOGGER.error("Failed to close storage", (Throwable)exception);
        }
    }

    private /* synthetic */ void method_27939(MessageListener messageListener, Supplier supplier) {
        if (!this.closed.get()) {
            messageListener.send((Either)supplier.get());
        }
        this.writeRemainingResults();
    }

    static enum Priority {
        FOREGROUND,
        BACKGROUND,
        SHUTDOWN;

    }

    static class Result {
        @Nullable
        NbtCompound nbt;
        final CompletableFuture<Void> future = new CompletableFuture();

        public Result(@Nullable NbtCompound nbt) {
            this.nbt = nbt;
        }
    }
}

