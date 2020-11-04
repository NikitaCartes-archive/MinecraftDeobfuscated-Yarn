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
import net.minecraft.nbt.CompoundTag;
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
    private final TaskExecutor<TaskQueue.PrioritizedTask> field_24468;
    private final RegionBasedStorage storage;
    private final Map<ChunkPos, Result> results = Maps.newLinkedHashMap();

    protected StorageIoWorker(File file, boolean bl, String string) {
        this.storage = new RegionBasedStorage(file, bl);
        this.field_24468 = new TaskExecutor<TaskQueue.PrioritizedTask>(new TaskQueue.Prioritized(Priority.values().length), Util.getIoWorkerExecutor(), "IOWorker-" + string);
    }

    public CompletableFuture<Void> setResult(ChunkPos pos, @Nullable CompoundTag nbt) {
        return this.run(() -> {
            Result result = this.results.computeIfAbsent(pos, chunkPos -> new Result(nbt));
            result.nbt = nbt;
            return Either.left(result.future);
        }).thenCompose(Function.identity());
    }

    @Nullable
    public CompoundTag getNbt(ChunkPos pos) throws IOException {
        CompletableFuture<CompoundTag> completableFuture = this.method_31738(pos);
        try {
            return completableFuture.join();
        } catch (CompletionException completionException) {
            if (completionException.getCause() instanceof IOException) {
                throw (IOException)completionException.getCause();
            }
            throw completionException;
        }
    }

    protected CompletableFuture<CompoundTag> method_31738(ChunkPos chunkPos) {
        return this.run(() -> {
            Result result = this.results.get(chunkPos);
            if (result != null) {
                return Either.left(result.nbt);
            }
            try {
                CompoundTag compoundTag = this.storage.getTagAt(chunkPos);
                return Either.left(compoundTag);
            } catch (Exception exception) {
                LOGGER.warn("Failed to read chunk {}", (Object)chunkPos, (Object)exception);
                return Either.right(exception);
            }
        });
    }

    public CompletableFuture<Void> completeAll() {
        CompletionStage completableFuture = this.run(() -> Either.left(CompletableFuture.allOf((CompletableFuture[])this.results.values().stream().map(result -> ((Result)result).future).toArray(CompletableFuture[]::new)))).thenCompose(Function.identity());
        return ((CompletableFuture)completableFuture).thenCompose(void_ -> this.run(() -> {
            try {
                this.storage.method_26982();
                return Either.left(null);
            } catch (Exception exception) {
                LOGGER.warn("Failed to synchronized chunks", (Throwable)exception);
                return Either.right(exception);
            }
        }));
    }

    private <T> CompletableFuture<T> run(Supplier<Either<T, Exception>> supplier) {
        return this.field_24468.method_27918(messageListener -> new TaskQueue.PrioritizedTask(Priority.FOREGROUND.ordinal(), () -> this.method_27939(messageListener, (Supplier)supplier)));
    }

    private void writeResult() {
        Iterator<Map.Entry<ChunkPos, Result>> iterator = this.results.entrySet().iterator();
        if (!iterator.hasNext()) {
            return;
        }
        Map.Entry<ChunkPos, Result> entry = iterator.next();
        iterator.remove();
        this.write(entry.getKey(), entry.getValue());
        this.method_27945();
    }

    private void method_27945() {
        this.field_24468.send(new TaskQueue.PrioritizedTask(Priority.BACKGROUND.ordinal(), this::writeResult));
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
        this.field_24468.ask(messageListener -> new TaskQueue.PrioritizedTask(Priority.SHUTDOWN.ordinal(), () -> messageListener.send(Unit.INSTANCE))).join();
        this.field_24468.close();
        try {
            this.storage.close();
        } catch (Exception exception) {
            LOGGER.error("Failed to close storage", (Throwable)exception);
        }
    }

    private /* synthetic */ void method_27939(MessageListener messageListener, Supplier supplier) {
        if (!this.closed.get()) {
            messageListener.send(supplier.get());
        }
        this.method_27945();
    }

    static class Result {
        @Nullable
        private CompoundTag nbt;
        private final CompletableFuture<Void> future = new CompletableFuture();

        public Result(@Nullable CompoundTag compoundTag) {
            this.nbt = compoundTag;
        }
    }

    static enum Priority {
        FOREGROUND,
        BACKGROUND,
        SHUTDOWN;

    }
}

