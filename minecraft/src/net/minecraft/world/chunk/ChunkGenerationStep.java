package net.minecraft.world.chunk;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.util.function.Finishable;
import net.minecraft.util.profiling.jfr.FlightProfiler;

public record ChunkGenerationStep(
	ChunkStatus targetStatus,
	GenerationDependencies directDependencies,
	GenerationDependencies accumulatedDependencies,
	int blockStateWriteRadius,
	GenerationTask task
) {

	public int getAdditionalLevel(ChunkStatus status) {
		return status == this.targetStatus ? 0 : this.accumulatedDependencies.getAdditionalLevel(status);
	}

	public CompletableFuture<Chunk> run(ChunkGenerationContext context, BoundedRegionArray<AbstractChunkHolder> boundedRegionArray, Chunk chunk) {
		if (chunk.getStatus().isEarlierThan(this.targetStatus)) {
			Finishable finishable = FlightProfiler.INSTANCE.startChunkGenerationProfiling(chunk.getPos(), context.world().getRegistryKey(), this.targetStatus.getId());
			return this.task.doWork(context, this, boundedRegionArray, chunk).thenApply(generated -> this.finalizeGeneration(generated, finishable));
		} else {
			return this.task.doWork(context, this, boundedRegionArray, chunk);
		}
	}

	private Chunk finalizeGeneration(Chunk chunk, @Nullable Finishable finishCallback) {
		if (chunk instanceof ProtoChunk protoChunk && protoChunk.getStatus().isEarlierThan(this.targetStatus)) {
			protoChunk.setStatus(this.targetStatus);
		}

		if (finishCallback != null) {
			finishCallback.finish();
		}

		return chunk;
	}

	public static class Builder {
		private final ChunkStatus targetStatus;
		@Nullable
		private final ChunkGenerationStep previousStep;
		private ChunkStatus[] directDependencies;
		private int blockStateWriteRadius = -1;
		private GenerationTask task = ChunkGenerating::noop;

		protected Builder(ChunkStatus targetStatus) {
			if (targetStatus.getPrevious() != targetStatus) {
				throw new IllegalArgumentException("Not starting with the first status: " + targetStatus);
			} else {
				this.targetStatus = targetStatus;
				this.previousStep = null;
				this.directDependencies = new ChunkStatus[0];
			}
		}

		protected Builder(ChunkStatus blockStateWriteRadius, ChunkGenerationStep previousStep) {
			if (previousStep.targetStatus.getIndex() != blockStateWriteRadius.getIndex() - 1) {
				throw new IllegalArgumentException("Out of order status: " + blockStateWriteRadius);
			} else {
				this.targetStatus = blockStateWriteRadius;
				this.previousStep = previousStep;
				this.directDependencies = new ChunkStatus[]{previousStep.targetStatus};
			}
		}

		public ChunkGenerationStep.Builder dependsOn(ChunkStatus status, int level) {
			if (status.isAtLeast(this.targetStatus)) {
				throw new IllegalArgumentException("Status " + status + " can not be required by " + this.targetStatus);
			} else {
				ChunkStatus[] chunkStatuss = this.directDependencies;
				int i = level + 1;
				if (i > chunkStatuss.length) {
					this.directDependencies = new ChunkStatus[i];
					Arrays.fill(this.directDependencies, status);
				}

				for (int j = 0; j < Math.min(i, chunkStatuss.length); j++) {
					this.directDependencies[j] = ChunkStatus.max(chunkStatuss[j], status);
				}

				return this;
			}
		}

		public ChunkGenerationStep.Builder blockStateWriteRadius(int blockStateWriteRadius) {
			this.blockStateWriteRadius = blockStateWriteRadius;
			return this;
		}

		public ChunkGenerationStep.Builder task(GenerationTask task) {
			this.task = task;
			return this;
		}

		public ChunkGenerationStep build() {
			return new ChunkGenerationStep(
				this.targetStatus,
				new GenerationDependencies(ImmutableList.copyOf(this.directDependencies)),
				new GenerationDependencies(ImmutableList.copyOf(this.accumulateDependencies())),
				this.blockStateWriteRadius,
				this.task
			);
		}

		private ChunkStatus[] accumulateDependencies() {
			if (this.previousStep == null) {
				return this.directDependencies;
			} else {
				int i = this.getParentStatus(this.previousStep.targetStatus);
				GenerationDependencies generationDependencies = this.previousStep.accumulatedDependencies;
				ChunkStatus[] chunkStatuss = new ChunkStatus[Math.max(i + generationDependencies.size(), this.directDependencies.length)];

				for (int j = 0; j < chunkStatuss.length; j++) {
					int k = j - i;
					if (k < 0 || k >= generationDependencies.size()) {
						chunkStatuss[j] = this.directDependencies[j];
					} else if (j >= this.directDependencies.length) {
						chunkStatuss[j] = generationDependencies.get(k);
					} else {
						chunkStatuss[j] = ChunkStatus.max(this.directDependencies[j], generationDependencies.get(k));
					}
				}

				return chunkStatuss;
			}
		}

		private int getParentStatus(ChunkStatus status) {
			for (int i = this.directDependencies.length - 1; i >= 0; i--) {
				if (this.directDependencies[i].isAtLeast(status)) {
					return i;
				}
			}

			return 0;
		}
	}
}
