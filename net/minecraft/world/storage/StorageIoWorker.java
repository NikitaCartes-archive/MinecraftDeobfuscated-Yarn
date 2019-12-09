/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.RegionBasedStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class StorageIoWorker
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Thread thread;
    private final AtomicBoolean closed = new AtomicBoolean();
    private final Queue<Runnable> tasks = Queues.newConcurrentLinkedQueue();
    private final RegionBasedStorage storage;
    private final Map<ChunkPos, Result> results = Maps.newLinkedHashMap();
    private boolean active = true;
    private CompletableFuture<Void> future = new CompletableFuture();

    StorageIoWorker(RegionBasedStorage storage, String name) {
        this.storage = storage;
        this.thread = new Thread(this::work);
        this.thread.setName(name + " IO worker");
        this.thread.start();
    }

    public CompletableFuture<Void> setResult(ChunkPos pos, CompoundTag nbt) {
        return this.run(completableFuture -> () -> {
            Result result = this.results.computeIfAbsent(pos, chunkPos -> new Result());
            result.nbt = nbt;
            result.future.whenComplete((void_, throwable) -> {
                if (throwable != null) {
                    completableFuture.completeExceptionally((Throwable)throwable);
                } else {
                    completableFuture.complete(null);
                }
            });
        });
    }

    @Nullable
    public CompoundTag getNbt(ChunkPos pos) throws IOException {
        CompletableFuture completableFuture2 = this.run(completableFuture -> () -> {
            Result result = this.results.get(pos);
            if (result != null) {
                completableFuture.complete(result.nbt);
            } else {
                try {
                    CompoundTag compoundTag = this.storage.getTagAt(pos);
                    completableFuture.complete(compoundTag);
                } catch (Exception exception) {
                    LOGGER.warn("Failed to read chunk {}", (Object)pos, (Object)exception);
                    completableFuture.completeExceptionally(exception);
                }
            }
        });
        try {
            return (CompoundTag)completableFuture2.join();
        } catch (CompletionException completionException) {
            if (completionException.getCause() instanceof IOException) {
                throw (IOException)completionException.getCause();
            }
            throw completionException;
        }
    }

    private CompletableFuture<Void> shutdown() {
        return this.run(completableFuture -> () -> {
            this.active = false;
            this.future = completableFuture;
        });
    }

    public CompletableFuture<Void> completeAll() {
        return this.run(completableFuture -> () -> {
            CompletableFuture<Void> completableFuture2 = CompletableFuture.allOf((CompletableFuture[])this.results.values().stream().map(result -> ((Result)result).future).toArray(CompletableFuture[]::new));
            completableFuture2.whenComplete((object, throwable) -> completableFuture.complete(null));
        });
    }

    private <T> CompletableFuture<T> run(Function<CompletableFuture<T>, Runnable> taskFactory) {
        CompletableFuture completableFuture = new CompletableFuture();
        this.tasks.add(taskFactory.apply(completableFuture));
        LockSupport.unpark(this.thread);
        return completableFuture;
    }

    private void park() {
        LockSupport.park("waiting for tasks");
    }

    private void work() {
        try {
            while (this.active) {
                boolean bl = this.runTask();
                boolean bl2 = this.writeResult();
                if (bl || bl2) continue;
                this.park();
            }
            this.runTask();
            this.writeAll();
        } finally {
            this.finish();
        }
    }

    private boolean writeResult() {
        Iterator<Map.Entry<ChunkPos, Result>> iterator = this.results.entrySet().iterator();
        if (!iterator.hasNext()) {
            return false;
        }
        Map.Entry<ChunkPos, Result> entry = iterator.next();
        iterator.remove();
        this.write(entry.getKey(), entry.getValue());
        return true;
    }

    private void writeAll() {
        this.results.forEach(this::write);
        this.results.clear();
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

    private void finish() {
        try {
            this.storage.close();
            this.future.complete(null);
        } catch (Exception exception) {
            LOGGER.error("Failed to close storage", (Throwable)exception);
            this.future.completeExceptionally(exception);
        }
    }

    private boolean runTask() {
        Runnable runnable;
        boolean bl = false;
        while ((runnable = this.tasks.poll()) != null) {
            bl = true;
            runnable.run();
        }
        return bl;
    }

    @Override
    public void close() throws IOException {
        if (!this.closed.compareAndSet(false, true)) {
            return;
        }
        try {
            this.shutdown().join();
        } catch (CompletionException completionException) {
            if (completionException.getCause() instanceof IOException) {
                throw (IOException)completionException.getCause();
            }
            throw completionException;
        }
    }

    static class Result {
        private CompoundTag nbt;
        private final CompletableFuture<Void> future = new CompletableFuture();

        private Result() {
        }
    }
}

