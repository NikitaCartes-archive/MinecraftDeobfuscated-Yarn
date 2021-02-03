package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.TaskQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StorageIoWorker implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final AtomicBoolean closed = new AtomicBoolean();
	private final TaskExecutor<TaskQueue.PrioritizedTask> executor;
	private final RegionBasedStorage storage;
	private final Map<ChunkPos, StorageIoWorker.Result> results = Maps.<ChunkPos, StorageIoWorker.Result>newLinkedHashMap();

	public StorageIoWorker(File directory, boolean dsync, String name) {
		this.storage = new RegionBasedStorage(directory, dsync);
		this.executor = new TaskExecutor<>(new TaskQueue.Prioritized(StorageIoWorker.Priority.values().length), Util.getIoWorkerExecutor(), "IOWorker-" + name);
	}

	public CompletableFuture<Void> setResult(ChunkPos pos, @Nullable CompoundTag nbt) {
		return this.run(() -> {
			StorageIoWorker.Result result = (StorageIoWorker.Result)this.results.computeIfAbsent(pos, chunkPosx -> new StorageIoWorker.Result(nbt));
			result.nbt = nbt;
			return Either.left(result.future);
		}).thenCompose(Function.identity());
	}

	@Nullable
	public CompoundTag getNbt(ChunkPos pos) throws IOException {
		CompletableFuture<CompoundTag> completableFuture = this.method_31738(pos);

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

	public CompletableFuture<CompoundTag> method_31738(ChunkPos chunkPos) {
		return this.run(() -> {
			StorageIoWorker.Result result = (StorageIoWorker.Result)this.results.get(chunkPos);
			if (result != null) {
				return Either.left(result.nbt);
			} else {
				try {
					CompoundTag compoundTag = this.storage.getTagAt(chunkPos);
					return Either.left(compoundTag);
				} catch (Exception var4) {
					LOGGER.warn("Failed to read chunk {}", chunkPos, var4);
					return Either.right(var4);
				}
			}
		});
	}

	public CompletableFuture<Void> completeAll() {
		CompletableFuture<Void> completableFuture = this.run(
				() -> Either.left(
						CompletableFuture.allOf((CompletableFuture[])this.results.values().stream().map(result -> result.future).toArray(CompletableFuture[]::new))
					)
			)
			.thenCompose(Function.identity());
		return completableFuture.thenCompose(void_ -> this.run(() -> {
				try {
					this.storage.method_26982();
					return Either.left(null);
				} catch (Exception var2) {
					LOGGER.warn("Failed to synchronized chunks", (Throwable)var2);
					return Either.right(var2);
				}
			}));
	}

	private <T> CompletableFuture<T> run(Supplier<Either<T, Exception>> supplier) {
		return this.executor.method_27918(messageListener -> new TaskQueue.PrioritizedTask(StorageIoWorker.Priority.FOREGROUND.ordinal(), () -> {
				if (!this.closed.get()) {
					messageListener.send(supplier.get());
				}

				this.method_27945();
			}));
	}

	private void writeResult() {
		if (!this.results.isEmpty()) {
			Iterator<Entry<ChunkPos, StorageIoWorker.Result>> iterator = this.results.entrySet().iterator();
			Entry<ChunkPos, StorageIoWorker.Result> entry = (Entry<ChunkPos, StorageIoWorker.Result>)iterator.next();
			iterator.remove();
			this.write((ChunkPos)entry.getKey(), (StorageIoWorker.Result)entry.getValue());
			this.method_27945();
		}
	}

	private void method_27945() {
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
			this.executor
				.ask(messageListener -> new TaskQueue.PrioritizedTask(StorageIoWorker.Priority.SHUTDOWN.ordinal(), () -> messageListener.send(Unit.INSTANCE)))
				.join();
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
		private CompoundTag nbt;
		private final CompletableFuture<Void> future = new CompletableFuture();

		public Result(@Nullable CompoundTag compoundTag) {
			this.nbt = compoundTag;
		}
	}
}
