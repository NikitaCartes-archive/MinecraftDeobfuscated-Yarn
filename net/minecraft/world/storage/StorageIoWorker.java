/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.IOException;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.scanner.NbtScanQuery;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.scanner.SelectiveNbtCollector;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.TaskQueue;
import net.minecraft.world.storage.NbtScannable;
import net.minecraft.world.storage.RegionBasedStorage;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class StorageIoWorker
implements NbtScannable,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final AtomicBoolean closed = new AtomicBoolean();
    private final TaskExecutor<TaskQueue.PrioritizedTask> executor;
    private final RegionBasedStorage storage;
    private final Map<ChunkPos, Result> results = Maps.newLinkedHashMap();
    private final Long2ObjectLinkedOpenHashMap<CompletableFuture<BitSet>> blendingStatusCaches = new Long2ObjectLinkedOpenHashMap();
    private static final int MAX_CACHE_SIZE = 1024;

    protected StorageIoWorker(Path directory, boolean dsync, String name) {
        this.storage = new RegionBasedStorage(directory, dsync);
        this.executor = new TaskExecutor<TaskQueue.PrioritizedTask>(new TaskQueue.Prioritized(Priority.values().length), Util.getIoWorkerExecutor(), "IOWorker-" + name);
    }

    public boolean needsBlending(ChunkPos chunkPos, int checkRadius) {
        ChunkPos chunkPos2 = new ChunkPos(chunkPos.x - checkRadius, chunkPos.z - checkRadius);
        ChunkPos chunkPos3 = new ChunkPos(chunkPos.x + checkRadius, chunkPos.z + checkRadius);
        for (int i = chunkPos2.getRegionX(); i <= chunkPos3.getRegionX(); ++i) {
            for (int j = chunkPos2.getRegionZ(); j <= chunkPos3.getRegionZ(); ++j) {
                BitSet bitSet = this.getOrComputeBlendingStatus(i, j).join();
                if (bitSet.isEmpty()) continue;
                ChunkPos chunkPos4 = ChunkPos.fromRegion(i, j);
                int k = Math.max(chunkPos2.x - chunkPos4.x, 0);
                int l = Math.max(chunkPos2.z - chunkPos4.z, 0);
                int m = Math.min(chunkPos3.x - chunkPos4.x, 31);
                int n = Math.min(chunkPos3.z - chunkPos4.z, 31);
                for (int o = k; o <= m; ++o) {
                    for (int p = l; p <= n; ++p) {
                        int q = p * 32 + o;
                        if (!bitSet.get(q)) continue;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CompletableFuture<BitSet> getOrComputeBlendingStatus(int chunkX, int chunkZ) {
        long l = ChunkPos.toLong(chunkX, chunkZ);
        Long2ObjectLinkedOpenHashMap<CompletableFuture<BitSet>> long2ObjectLinkedOpenHashMap = this.blendingStatusCaches;
        synchronized (long2ObjectLinkedOpenHashMap) {
            CompletableFuture<BitSet> completableFuture = this.blendingStatusCaches.getAndMoveToFirst(l);
            if (completableFuture == null) {
                completableFuture = this.computeBlendingStatus(chunkX, chunkZ);
                this.blendingStatusCaches.putAndMoveToFirst(l, completableFuture);
                if (this.blendingStatusCaches.size() > 1024) {
                    this.blendingStatusCaches.removeLast();
                }
            }
            return completableFuture;
        }
    }

    private CompletableFuture<BitSet> computeBlendingStatus(int chunkX, int chunkZ) {
        return CompletableFuture.supplyAsync(() -> {
            ChunkPos chunkPos2 = ChunkPos.fromRegion(chunkX, chunkZ);
            ChunkPos chunkPos22 = ChunkPos.fromRegionCenter(chunkX, chunkZ);
            BitSet bitSet = new BitSet();
            ChunkPos.stream(chunkPos2, chunkPos22).forEach(chunkPos -> {
                NbtCompound nbtCompound;
                SelectiveNbtCollector selectiveNbtCollector = new SelectiveNbtCollector(new NbtScanQuery(NbtInt.TYPE, "DataVersion"), new NbtScanQuery(NbtCompound.TYPE, "blending_data"));
                try {
                    this.scanChunk((ChunkPos)chunkPos, selectiveNbtCollector).join();
                } catch (Exception exception) {
                    LOGGER.warn("Failed to scan chunk {}", chunkPos, (Object)exception);
                    return;
                }
                NbtElement nbtElement = selectiveNbtCollector.getRoot();
                if (nbtElement instanceof NbtCompound && this.needsBlending(nbtCompound = (NbtCompound)nbtElement)) {
                    int i = chunkPos.getRegionRelativeZ() * 32 + chunkPos.getRegionRelativeX();
                    bitSet.set(i);
                }
            });
            return bitSet;
        }, Util.getMainWorkerExecutor());
    }

    private boolean needsBlending(NbtCompound nbt) {
        if (!nbt.contains("DataVersion", NbtElement.NUMBER_TYPE) || nbt.getInt("DataVersion") < 3088) {
            return true;
        }
        return nbt.contains("blending_data", NbtElement.COMPOUND_TYPE);
    }

    public CompletableFuture<Void> setResult(ChunkPos pos, @Nullable NbtCompound nbt) {
        return this.run(() -> {
            Result result = this.results.computeIfAbsent(pos, pos2 -> new Result(nbt));
            result.nbt = nbt;
            return Either.left(result.future);
        }).thenCompose(Function.identity());
    }

    public CompletableFuture<Optional<NbtCompound>> readChunkData(ChunkPos pos) {
        return this.run(() -> {
            Result result = this.results.get(pos);
            if (result != null) {
                return Either.left(Optional.ofNullable(result.nbt));
            }
            try {
                NbtCompound nbtCompound = this.storage.getTagAt(pos);
                return Either.left(Optional.ofNullable(nbtCompound));
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
                    LOGGER.warn("Failed to synchronize chunks", exception);
                    return Either.right(exception);
                }
            }));
        }
        return ((CompletableFuture)completableFuture).thenCompose(void_ -> this.run(() -> Either.left(null)));
    }

    @Override
    public CompletableFuture<Void> scanChunk(ChunkPos pos, NbtScanner scanner) {
        return this.run(() -> {
            try {
                Result result = this.results.get(pos);
                if (result != null) {
                    if (result.nbt != null) {
                        result.nbt.accept(scanner);
                    }
                } else {
                    this.storage.scanChunk(pos, scanner);
                }
                return Either.left(null);
            } catch (Exception exception) {
                LOGGER.warn("Failed to bulk scan chunk {}", (Object)pos, (Object)exception);
                return Either.right(exception);
            }
        });
    }

    private <T> CompletableFuture<T> run(Supplier<Either<T, Exception>> task) {
        return this.executor.askFallible(listener -> new TaskQueue.PrioritizedTask(Priority.FOREGROUND.ordinal(), () -> this.method_27939(listener, (Supplier)task)));
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
        this.executor.ask(listener -> new TaskQueue.PrioritizedTask(Priority.SHUTDOWN.ordinal(), () -> listener.send(Unit.INSTANCE))).join();
        this.executor.close();
        try {
            this.storage.close();
        } catch (Exception exception) {
            LOGGER.error("Failed to close storage", exception);
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

