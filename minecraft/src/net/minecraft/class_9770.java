package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.jfr.Finishable;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkGenerating;
import net.minecraft.world.chunk.ChunkGenerationContext;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.GenerationTask;
import net.minecraft.world.chunk.ProtoChunk;

public record class_9770(
	ChunkStatus targetStatus, class_9767 directDependencies, class_9767 accumulatedDependencies, int blockStateWriteRadius, GenerationTask task
) {

	public int method_60559(ChunkStatus chunkStatus) {
		return chunkStatus == this.targetStatus ? 0 : this.accumulatedDependencies.method_60515(chunkStatus);
	}

	public CompletableFuture<Chunk> method_60560(ChunkGenerationContext chunkGenerationContext, class_9762<class_9761> arg, Chunk chunk) {
		if (chunk.getStatus().method_60549(this.targetStatus)) {
			Finishable finishable = FlightProfiler.INSTANCE
				.startChunkGenerationProfiling(chunk.getPos(), chunkGenerationContext.world().getRegistryKey(), this.targetStatus.method_60550());
			return this.task.doWork(chunkGenerationContext, this, arg, chunk).thenApply(chunkx -> this.method_60558(chunkx, finishable));
		} else {
			return this.task.doWork(chunkGenerationContext, this, arg, chunk);
		}
	}

	private Chunk method_60558(Chunk chunk, @Nullable Finishable finishable) {
		if (chunk instanceof ProtoChunk protoChunk && protoChunk.getStatus().method_60549(this.targetStatus)) {
			protoChunk.setStatus(this.targetStatus);
		}

		if (finishable != null) {
			finishable.finish();
		}

		return chunk;
	}

	public static class class_9771 {
		private final ChunkStatus field_51905;
		@Nullable
		private final class_9770 field_51906;
		private ChunkStatus[] field_51907;
		private int field_51908 = -1;
		private GenerationTask field_51909 = ChunkGenerating::noop;

		protected class_9771(ChunkStatus chunkStatus) {
			if (chunkStatus.getPrevious() != chunkStatus) {
				throw new IllegalArgumentException("Not starting with the first status: " + chunkStatus);
			} else {
				this.field_51905 = chunkStatus;
				this.field_51906 = null;
				this.field_51907 = new ChunkStatus[0];
			}
		}

		protected class_9771(ChunkStatus chunkStatus, class_9770 arg) {
			if (arg.targetStatus.getIndex() != chunkStatus.getIndex() - 1) {
				throw new IllegalArgumentException("Out of order status: " + chunkStatus);
			} else {
				this.field_51905 = chunkStatus;
				this.field_51906 = arg;
				this.field_51907 = new ChunkStatus[]{arg.targetStatus};
			}
		}

		public class_9770.class_9771 method_60564(ChunkStatus chunkStatus, int i) {
			if (chunkStatus.isAtLeast(this.field_51905)) {
				throw new IllegalArgumentException("Status " + chunkStatus + " can not be required by " + this.field_51905);
			} else {
				ChunkStatus[] chunkStatuss = this.field_51907;
				int j = i + 1;
				if (j > chunkStatuss.length) {
					this.field_51907 = new ChunkStatus[j];
					Arrays.fill(this.field_51907, chunkStatus);
				}

				for (int k = 0; k < Math.min(j, chunkStatuss.length); k++) {
					this.field_51907[k] = ChunkStatus.method_60545(chunkStatuss[k], chunkStatus);
				}

				return this;
			}
		}

		public class_9770.class_9771 method_60562(int i) {
			this.field_51908 = i;
			return this;
		}

		public class_9770.class_9771 method_60565(GenerationTask generationTask) {
			this.field_51909 = generationTask;
			return this;
		}

		public class_9770 method_60561() {
			return new class_9770(
				this.field_51905,
				new class_9767(ImmutableList.copyOf(this.field_51907)),
				new class_9767(ImmutableList.copyOf(this.method_60566())),
				this.field_51908,
				this.field_51909
			);
		}

		private ChunkStatus[] method_60566() {
			if (this.field_51906 == null) {
				return this.field_51907;
			} else {
				int i = this.method_60563(this.field_51906.targetStatus);
				class_9767 lv = this.field_51906.accumulatedDependencies;
				ChunkStatus[] chunkStatuss = new ChunkStatus[Math.max(i + lv.method_60516(), this.field_51907.length)];

				for (int j = 0; j < chunkStatuss.length; j++) {
					int k = j - i;
					if (k < 0 || k >= lv.method_60516()) {
						chunkStatuss[j] = this.field_51907[j];
					} else if (j >= this.field_51907.length) {
						chunkStatuss[j] = lv.method_60514(k);
					} else {
						chunkStatuss[j] = ChunkStatus.method_60545(this.field_51907[j], lv.method_60514(k));
					}
				}

				return chunkStatuss;
			}
		}

		private int method_60563(ChunkStatus chunkStatus) {
			for (int i = this.field_51907.length - 1; i >= 0; i--) {
				if (this.field_51907[i].isAtLeast(chunkStatus)) {
					return i;
				}
			}

			return 0;
		}
	}
}
