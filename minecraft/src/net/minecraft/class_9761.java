package net.minecraft;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.server.world.ChunkLevels;
import net.minecraft.server.world.OptionalChunk;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WrapperProtoChunk;

public abstract class class_9761 {
	private static final List<ChunkStatus> field_51865 = ChunkStatus.createOrderedList();
	private static final OptionalChunk<Chunk> field_51869 = OptionalChunk.of("Not done yet");
	public static final OptionalChunk<Chunk> field_51866 = OptionalChunk.of("Unloaded chunk");
	public static final CompletableFuture<OptionalChunk<Chunk>> field_51867 = CompletableFuture.completedFuture(field_51866);
	protected final ChunkPos field_51868;
	@Nullable
	private volatile ChunkStatus field_51870;
	private final AtomicReference<ChunkStatus> field_51871 = new AtomicReference();
	private final AtomicReferenceArray<CompletableFuture<OptionalChunk<Chunk>>> field_51872 = new AtomicReferenceArray(field_51865.size());
	private final AtomicReference<class_9759> field_51873 = new AtomicReference();
	private final AtomicInteger field_51874 = new AtomicInteger();

	public class_9761(ChunkPos chunkPos) {
		this.field_51868 = chunkPos;
	}

	public CompletableFuture<OptionalChunk<Chunk>> method_60458(ChunkStatus chunkStatus, ThreadedAnvilChunkStorage threadedAnvilChunkStorage) {
		if (this.method_60467(chunkStatus)) {
			return field_51867;
		} else {
			CompletableFuture<OptionalChunk<Chunk>> completableFuture = this.method_60464(chunkStatus);
			if (completableFuture.isDone()) {
				return completableFuture;
			} else {
				class_9759 lv = (class_9759)this.field_51873.get();
				if (lv == null || chunkStatus.method_60547(lv.field_51850)) {
					this.method_60455(threadedAnvilChunkStorage, chunkStatus);
				}

				return completableFuture;
			}
		}
	}

	CompletableFuture<OptionalChunk<Chunk>> method_60461(class_9770 arg, class_9760 arg2, class_9762<class_9761> arg3) {
		if (this.method_60467(arg.targetStatus())) {
			return field_51867;
		} else {
			return this.method_60466(arg.targetStatus()) ? arg2.method_60442(this, arg, arg3).handle((chunk, throwable) -> {
				if (throwable != null) {
					CrashReport crashReport = CrashReport.create(throwable, "Exception chunk generation/loading");
					MinecraftServer.method_60582(new CrashException(crashReport));
				} else {
					this.method_60459(arg.targetStatus(), chunk);
				}

				return OptionalChunk.of(chunk);
			}) : this.method_60464(arg.targetStatus());
		}
	}

	public void method_60454(ThreadedAnvilChunkStorage threadedAnvilChunkStorage) {
		ChunkStatus chunkStatus = this.field_51870;
		ChunkStatus chunkStatus2 = ChunkLevels.getStatus(this.getLevel());
		this.field_51870 = chunkStatus2;
		boolean bl = chunkStatus != null && (chunkStatus2 == null || chunkStatus2.method_60549(chunkStatus));
		if (bl) {
			this.method_60460(chunkStatus2, chunkStatus);
			if (this.field_51873.get() != null) {
				this.method_60455(threadedAnvilChunkStorage, this.method_60465(chunkStatus2));
			}
		}
	}

	public void method_60456(WrapperProtoChunk wrapperProtoChunk) {
		CompletableFuture<OptionalChunk<Chunk>> completableFuture = CompletableFuture.completedFuture(OptionalChunk.of(wrapperProtoChunk));

		for (int i = 0; i < this.field_51872.length() - 1; i++) {
			CompletableFuture<OptionalChunk<Chunk>> completableFuture2 = (CompletableFuture<OptionalChunk<Chunk>>)this.field_51872.get(i);
			Objects.requireNonNull(completableFuture2);
			Chunk chunk = (Chunk)((OptionalChunk)completableFuture2.getNow(field_51869)).orElse(null);
			if (!(chunk instanceof ProtoChunk)) {
				throw new IllegalStateException("Trying to replace a ProtoChunk, but found " + chunk);
			}

			if (!this.field_51872.compareAndSet(i, completableFuture2, completableFuture)) {
				throw new IllegalStateException("Future changed by other thread while trying to replace it");
			}
		}
	}

	void method_60453(class_9759 arg) {
		this.field_51873.compareAndSet(arg, null);
	}

	private void method_60455(ThreadedAnvilChunkStorage threadedAnvilChunkStorage, @Nullable ChunkStatus chunkStatus) {
		class_9759 lv;
		if (chunkStatus != null) {
			lv = threadedAnvilChunkStorage.method_60443(chunkStatus, this.method_60473());
		} else {
			lv = null;
		}

		class_9759 lv2 = (class_9759)this.field_51873.getAndSet(lv);
		if (lv2 != null) {
			lv2.method_60429();
		}
	}

	private CompletableFuture<OptionalChunk<Chunk>> method_60464(ChunkStatus chunkStatus) {
		if (this.method_60467(chunkStatus)) {
			return field_51867;
		} else {
			int i = chunkStatus.getIndex();
			CompletableFuture<OptionalChunk<Chunk>> completableFuture = (CompletableFuture<OptionalChunk<Chunk>>)this.field_51872.get(i);

			while (completableFuture == null) {
				CompletableFuture<OptionalChunk<Chunk>> completableFuture2 = new CompletableFuture();
				completableFuture = (CompletableFuture<OptionalChunk<Chunk>>)this.field_51872.compareAndExchange(i, null, completableFuture2);
				if (completableFuture == null) {
					if (this.method_60467(chunkStatus)) {
						this.method_60452(i, completableFuture2);
						return field_51867;
					}

					return completableFuture2;
				}
			}

			return completableFuture;
		}
	}

	private void method_60460(@Nullable ChunkStatus chunkStatus, ChunkStatus chunkStatus2) {
		int i = chunkStatus == null ? 0 : chunkStatus.getIndex() + 1;
		int j = chunkStatus2.getIndex();

		for (int k = i; k <= j; k++) {
			CompletableFuture<OptionalChunk<Chunk>> completableFuture = (CompletableFuture<OptionalChunk<Chunk>>)this.field_51872.get(k);
			if (completableFuture != null) {
				this.method_60452(k, completableFuture);
			}
		}
	}

	private void method_60452(int i, CompletableFuture<OptionalChunk<Chunk>> completableFuture) {
		if (completableFuture.complete(field_51866) && !this.field_51872.compareAndSet(i, completableFuture, null)) {
			throw new IllegalStateException("Nothing else should replace the future here");
		}
	}

	private void method_60459(ChunkStatus chunkStatus, Chunk chunk) {
		OptionalChunk<Chunk> optionalChunk = OptionalChunk.of(chunk);
		int i = chunkStatus.getIndex();

		while (true) {
			CompletableFuture<OptionalChunk<Chunk>> completableFuture = (CompletableFuture<OptionalChunk<Chunk>>)this.field_51872.get(i);
			if (completableFuture == null) {
				if (this.field_51872.compareAndSet(i, null, CompletableFuture.completedFuture(optionalChunk))) {
					return;
				}
			} else {
				if (completableFuture.complete(optionalChunk)) {
					return;
				}

				if (((OptionalChunk)completableFuture.getNow(field_51869)).isPresent()) {
					throw new IllegalStateException("Trying to complete a future but found it to be completed successfully already");
				}

				Thread.yield();
			}
		}
	}

	@Nullable
	private ChunkStatus method_60465(@Nullable ChunkStatus chunkStatus) {
		if (chunkStatus == null) {
			return null;
		} else {
			ChunkStatus chunkStatus2 = chunkStatus;

			for (ChunkStatus chunkStatus3 = (ChunkStatus)this.field_51871.get();
				chunkStatus3 == null || chunkStatus2.method_60547(chunkStatus3);
				chunkStatus2 = chunkStatus2.getPrevious()
			) {
				if (this.field_51872.get(chunkStatus2.getIndex()) != null) {
					return chunkStatus2;
				}

				if (chunkStatus2 == ChunkStatus.EMPTY) {
					break;
				}
			}

			return null;
		}
	}

	private boolean method_60466(ChunkStatus chunkStatus) {
		ChunkStatus chunkStatus2 = chunkStatus == ChunkStatus.EMPTY ? null : chunkStatus.getPrevious();
		ChunkStatus chunkStatus3 = (ChunkStatus)this.field_51871.compareAndExchange(chunkStatus2, chunkStatus);
		if (chunkStatus3 == chunkStatus2) {
			return true;
		} else if (chunkStatus3 != null && !chunkStatus.method_60547(chunkStatus3)) {
			return false;
		} else {
			throw new IllegalStateException("Unexpected last startedWork status: " + chunkStatus3 + " while trying to start: " + chunkStatus);
		}
	}

	private boolean method_60467(ChunkStatus chunkStatus) {
		ChunkStatus chunkStatus2 = this.field_51870;
		return chunkStatus2 == null || chunkStatus.method_60547(chunkStatus2);
	}

	public void method_60468() {
		this.field_51874.incrementAndGet();
	}

	public void method_60469() {
		int i = this.field_51874.decrementAndGet();
		if (i < 0) {
			throw new IllegalStateException("More releases than claims. Count: " + i);
		}
	}

	public int method_60470() {
		return this.field_51874.get();
	}

	@Nullable
	public Chunk method_60457(ChunkStatus chunkStatus) {
		CompletableFuture<OptionalChunk<Chunk>> completableFuture = (CompletableFuture<OptionalChunk<Chunk>>)this.field_51872.get(chunkStatus.getIndex());
		return completableFuture == null ? null : (Chunk)((OptionalChunk)completableFuture.getNow(field_51869)).orElse(null);
	}

	@Nullable
	public Chunk method_60463(ChunkStatus chunkStatus) {
		return this.method_60467(chunkStatus) ? null : this.method_60457(chunkStatus);
	}

	@Nullable
	public Chunk method_60471() {
		ChunkStatus chunkStatus = (ChunkStatus)this.field_51871.get();
		if (chunkStatus == null) {
			return null;
		} else {
			Chunk chunk = this.method_60457(chunkStatus);
			return chunk != null ? chunk : this.method_60457(chunkStatus.getPrevious());
		}
	}

	@Nullable
	public ChunkStatus method_60472() {
		CompletableFuture<OptionalChunk<Chunk>> completableFuture = (CompletableFuture<OptionalChunk<Chunk>>)this.field_51872.get(ChunkStatus.EMPTY.getIndex());
		Chunk chunk = completableFuture == null ? null : (Chunk)((OptionalChunk)completableFuture.getNow(field_51869)).orElse(null);
		return chunk == null ? null : chunk.getStatus();
	}

	public ChunkPos method_60473() {
		return this.field_51868;
	}

	public ChunkLevelType method_60474() {
		return ChunkLevels.getType(this.getLevel());
	}

	public abstract int getLevel();

	public abstract int getCompletedLevel();

	@Debug
	public List<Pair<ChunkStatus, CompletableFuture<OptionalChunk<Chunk>>>> method_60475() {
		List<Pair<ChunkStatus, CompletableFuture<OptionalChunk<Chunk>>>> list = new ArrayList();

		for (int i = 0; i < field_51865.size(); i++) {
			list.add(Pair.of((ChunkStatus)field_51865.get(i), (CompletableFuture)this.field_51872.get(i)));
		}

		return list;
	}

	@Nullable
	@Debug
	public ChunkStatus method_60476() {
		for (int i = field_51865.size() - 1; i >= 0; i--) {
			ChunkStatus chunkStatus = (ChunkStatus)field_51865.get(i);
			Chunk chunk = this.method_60457(chunkStatus);
			if (chunk != null) {
				return chunkStatus;
			}
		}

		return null;
	}
}
