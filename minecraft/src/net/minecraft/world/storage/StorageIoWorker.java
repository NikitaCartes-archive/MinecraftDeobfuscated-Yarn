package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StorageIoWorker implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Thread thread;
	private final AtomicBoolean closed = new AtomicBoolean();
	private final Queue<Runnable> tasks = Queues.<Runnable>newConcurrentLinkedQueue();
	private final RegionBasedStorage storage;
	private final Map<ChunkPos, StorageIoWorker.Result> results = Maps.<ChunkPos, StorageIoWorker.Result>newLinkedHashMap();
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
				StorageIoWorker.Result result = (StorageIoWorker.Result)this.results.computeIfAbsent(pos, chunkPosxx -> new StorageIoWorker.Result());
				result.nbt = nbt;
				result.future.whenComplete((void_, throwable) -> {
					if (throwable != null) {
						completableFuture.completeExceptionally(throwable);
					} else {
						completableFuture.complete(null);
					}
				});
			});
	}

	@Nullable
	public CompoundTag getNbt(ChunkPos pos) throws IOException {
		CompletableFuture<CompoundTag> completableFuture = this.run(completableFuturex -> () -> {
				StorageIoWorker.Result result = (StorageIoWorker.Result)this.results.get(pos);
				if (result != null) {
					completableFuturex.complete(result.nbt);
				} else {
					try {
						CompoundTag compoundTag = this.storage.getTagAt(pos);
						completableFuturex.complete(compoundTag);
					} catch (Exception var5) {
						LOGGER.warn("Failed to read chunk {}", pos, var5);
						completableFuturex.completeExceptionally(var5);
					}
				}
			});

		try {
			return (CompoundTag)completableFuture.join();
		} catch (CompletionException var4) {
			if (var4.getCause() instanceof IOException) {
				throw (IOException)var4.getCause();
			} else {
				throw var4;
			}
		}
	}

	private CompletableFuture<Void> shutdown() {
		return this.run(completableFuture -> () -> {
				this.active = false;
				this.future = completableFuture;
			});
	}

	public CompletableFuture<Void> completeAll() {
		return this.run(
			completableFuture -> () -> {
					CompletableFuture<?> completableFuture2 = CompletableFuture.allOf(
						(CompletableFuture[])this.results.values().stream().map(result -> result.future).toArray(CompletableFuture[]::new)
					);
					completableFuture2.whenComplete((object, throwable) -> {
						try {
							this.storage.method_26982();
							completableFuture.complete(null);
						} catch (Exception var5) {
							LOGGER.warn("Failed to synchronized chunks", (Throwable)var5);
							completableFuture.completeExceptionally(var5);
						}
					});
				}
		);
	}

	private <T> CompletableFuture<T> run(Function<CompletableFuture<T>, Runnable> taskFactory) {
		CompletableFuture<T> completableFuture = new CompletableFuture();
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
				if (!bl && !bl2) {
					this.park();
				}
			}

			this.runTask();
			this.writeAll();
		} finally {
			this.finish();
		}
	}

	private boolean writeResult() {
		Iterator<Entry<ChunkPos, StorageIoWorker.Result>> iterator = this.results.entrySet().iterator();
		if (!iterator.hasNext()) {
			return false;
		} else {
			Entry<ChunkPos, StorageIoWorker.Result> entry = (Entry<ChunkPos, StorageIoWorker.Result>)iterator.next();
			iterator.remove();
			this.write((ChunkPos)entry.getKey(), (StorageIoWorker.Result)entry.getValue());
			return true;
		}
	}

	private void writeAll() {
		this.results.forEach(this::write);
		this.results.clear();
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

	private void finish() {
		try {
			this.storage.close();
			this.future.complete(null);
		} catch (Exception var2) {
			LOGGER.error("Failed to close storage", (Throwable)var2);
			this.future.completeExceptionally(var2);
		}
	}

	private boolean runTask() {
		boolean bl = false;

		Runnable runnable;
		while ((runnable = (Runnable)this.tasks.poll()) != null) {
			bl = true;
			runnable.run();
		}

		return bl;
	}

	public void close() throws IOException {
		if (this.closed.compareAndSet(false, true)) {
			try {
				this.shutdown().join();
			} catch (CompletionException var2) {
				if (var2.getCause() instanceof IOException) {
					throw (IOException)var2.getCause();
				} else {
					throw var2;
				}
			}
		}
	}

	static class Result {
		private CompoundTag nbt;
		private final CompletableFuture<Void> future = new CompletableFuture();

		private Result() {
		}
	}
}
