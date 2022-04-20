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
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.scanner.NbtScanQuery;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.scanner.SelectiveNbtCollector;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.TaskQueue;
import org.slf4j.Logger;

public class StorageIoWorker implements NbtScannable, AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final AtomicBoolean closed = new AtomicBoolean();
	private final TaskExecutor<TaskQueue.PrioritizedTask> executor;
	private final RegionBasedStorage storage;
	private final Map<ChunkPos, StorageIoWorker.Result> results = Maps.<ChunkPos, StorageIoWorker.Result>newLinkedHashMap();
	private final Long2ObjectLinkedOpenHashMap<CompletableFuture<BitSet>> blendingStatusCaches = new Long2ObjectLinkedOpenHashMap<>();
	private static final int MAX_CACHE_SIZE = 1024;

	protected StorageIoWorker(Path directory, boolean dsync, String name) {
		this.storage = new RegionBasedStorage(directory, dsync);
		this.executor = new TaskExecutor<>(new TaskQueue.Prioritized(StorageIoWorker.Priority.values().length), Util.getIoWorkerExecutor(), "IOWorker-" + name);
	}

	public boolean needsBlending(ChunkPos chunkPos, int checkRadius) {
		ChunkPos chunkPos2 = new ChunkPos(chunkPos.x - checkRadius, chunkPos.z - checkRadius);
		ChunkPos chunkPos3 = new ChunkPos(chunkPos.x + checkRadius, chunkPos.z + checkRadius);

		for (int i = chunkPos2.getRegionX(); i <= chunkPos3.getRegionX(); i++) {
			for (int j = chunkPos2.getRegionZ(); j <= chunkPos3.getRegionZ(); j++) {
				BitSet bitSet = (BitSet)this.getOrComputeBlendingStatus(i, j).join();
				if (!bitSet.isEmpty()) {
					ChunkPos chunkPos4 = ChunkPos.fromRegion(i, j);
					int k = Math.max(chunkPos2.x - chunkPos4.x, 0);
					int l = Math.max(chunkPos2.z - chunkPos4.z, 0);
					int m = Math.min(chunkPos3.x - chunkPos4.x, 31);
					int n = Math.min(chunkPos3.z - chunkPos4.z, 31);

					for (int o = k; o <= m; o++) {
						for (int p = l; p <= n; p++) {
							int q = p * 32 + o;
							if (bitSet.get(q)) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	private CompletableFuture<BitSet> getOrComputeBlendingStatus(int chunkX, int chunkZ) {
		long l = ChunkPos.toLong(chunkX, chunkZ);
		synchronized (this.blendingStatusCaches) {
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
		return CompletableFuture.supplyAsync(
			() -> {
				ChunkPos chunkPos = ChunkPos.fromRegion(chunkX, chunkZ);
				ChunkPos chunkPos2 = ChunkPos.fromRegionCenter(chunkX, chunkZ);
				BitSet bitSet = new BitSet();
				ChunkPos.stream(chunkPos, chunkPos2)
					.forEach(
						chunkPosx -> {
							SelectiveNbtCollector selectiveNbtCollector = new SelectiveNbtCollector(
								new NbtScanQuery(NbtInt.TYPE, "DataVersion"), new NbtScanQuery(NbtCompound.TYPE, "blending_data")
							);
							this.scanChunk(chunkPosx, selectiveNbtCollector).join();
							if (selectiveNbtCollector.getRoot() instanceof NbtCompound nbtCompound) {
								int ix = chunkPosx.getRegionRelativeZ() * 32 + chunkPosx.getRegionRelativeX();
								bitSet.set(ix, this.needsBlending(nbtCompound));
							}
						}
					);
				return bitSet;
			},
			Util.getMainWorkerExecutor()
		);
	}

	private boolean needsBlending(NbtCompound nbt) {
		return nbt.contains("DataVersion", NbtElement.NUMBER_TYPE) && nbt.getInt("DataVersion") >= 3088
			? nbt.contains("blending_data", NbtElement.COMPOUND_TYPE)
			: true;
	}

	public CompletableFuture<Void> setResult(ChunkPos pos, @Nullable NbtCompound nbt) {
		return this.run(() -> {
			StorageIoWorker.Result result = (StorageIoWorker.Result)this.results.computeIfAbsent(pos, pos2 -> new StorageIoWorker.Result(nbt));
			result.nbt = nbt;
			return Either.left(result.future);
		}).thenCompose(Function.identity());
	}

	public CompletableFuture<Optional<NbtCompound>> readChunkData(ChunkPos pos) {
		return this.run(() -> {
			StorageIoWorker.Result result = (StorageIoWorker.Result)this.results.get(pos);
			if (result != null) {
				return Either.left(Optional.ofNullable(result.nbt));
			} else {
				try {
					NbtCompound nbtCompound = this.storage.getTagAt(pos);
					return Either.left(Optional.ofNullable(nbtCompound));
				} catch (Exception var4) {
					LOGGER.warn("Failed to read chunk {}", pos, var4);
					return Either.right(var4);
				}
			}
		});
	}

	public CompletableFuture<Void> completeAll(boolean sync) {
		CompletableFuture<Void> completableFuture = this.run(
				() -> Either.left(
						CompletableFuture.allOf((CompletableFuture[])this.results.values().stream().map(result -> result.future).toArray(CompletableFuture[]::new))
					)
			)
			.thenCompose(Function.identity());
		return sync ? completableFuture.thenCompose(void_ -> this.run(() -> {
				try {
					this.storage.sync();
					return Either.left(null);
				} catch (Exception var2x) {
					LOGGER.warn("Failed to synchronize chunks", (Throwable)var2x);
					return Either.right(var2x);
				}
			})) : completableFuture.thenCompose(void_ -> this.run(() -> Either.left(null)));
	}

	@Override
	public CompletableFuture<Void> scanChunk(ChunkPos pos, NbtScanner scanner) {
		return this.run(() -> {
			try {
				StorageIoWorker.Result result = (StorageIoWorker.Result)this.results.get(pos);
				if (result != null) {
					if (result.nbt != null) {
						result.nbt.accept(scanner);
					}
				} else {
					this.storage.method_39802(pos, scanner);
				}

				return Either.left(null);
			} catch (Exception var4) {
				LOGGER.warn("Failed to bulk scan chunk {}", pos, var4);
				return Either.right(var4);
			}
		});
	}

	private <T> CompletableFuture<T> run(Supplier<Either<T, Exception>> task) {
		return this.executor.askFallible(listener -> new TaskQueue.PrioritizedTask(StorageIoWorker.Priority.FOREGROUND.ordinal(), () -> {
				if (!this.closed.get()) {
					listener.send((Either)task.get());
				}

				this.writeRemainingResults();
			}));
	}

	private void writeResult() {
		if (!this.results.isEmpty()) {
			Iterator<Entry<ChunkPos, StorageIoWorker.Result>> iterator = this.results.entrySet().iterator();
			Entry<ChunkPos, StorageIoWorker.Result> entry = (Entry<ChunkPos, StorageIoWorker.Result>)iterator.next();
			iterator.remove();
			this.write((ChunkPos)entry.getKey(), (StorageIoWorker.Result)entry.getValue());
			this.writeRemainingResults();
		}
	}

	private void writeRemainingResults() {
		this.executor.send(new TaskQueue.PrioritizedTask(StorageIoWorker.Priority.BACKGROUND.ordinal(), this::writeResult));
	}

	private void write(ChunkPos pos, StorageIoWorker.Result result) {
		try {
			this.storage.write(pos, result.nbt);
			result.future.complete(null);
		} catch (Exception var4) {
			LOGGER.error("Failed to store chunk {}", pos, var4);
			result.future.completeExceptionally(var4);
		}
	}

	public void close() throws IOException {
		if (this.closed.compareAndSet(false, true)) {
			this.executor.ask(listener -> new TaskQueue.PrioritizedTask(StorageIoWorker.Priority.SHUTDOWN.ordinal(), () -> listener.send(Unit.INSTANCE))).join();
			this.executor.close();

			try {
				this.storage.close();
			} catch (Exception var2) {
				LOGGER.error("Failed to close storage", (Throwable)var2);
			}
		}
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
