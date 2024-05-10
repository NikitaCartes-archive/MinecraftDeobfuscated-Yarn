package net.minecraft.world.chunk;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.util.Locale;

public final class GenerationDependencies {
	private final ImmutableList<ChunkStatus> dependencies;
	private final int[] additionalLevelsByStatus;

	public GenerationDependencies(ImmutableList<ChunkStatus> dependencies) {
		this.dependencies = dependencies;
		int i = dependencies.isEmpty() ? 0 : ((ChunkStatus)dependencies.getFirst()).getIndex() + 1;
		this.additionalLevelsByStatus = new int[i];

		for (int j = 0; j < dependencies.size(); j++) {
			ChunkStatus chunkStatus = (ChunkStatus)dependencies.get(j);
			int k = chunkStatus.getIndex();

			for (int l = 0; l <= k; l++) {
				this.additionalLevelsByStatus[l] = j;
			}
		}
	}

	@VisibleForTesting
	public ImmutableList<ChunkStatus> getDependencies() {
		return this.dependencies;
	}

	public int size() {
		return this.dependencies.size();
	}

	public int getAdditionalLevel(ChunkStatus status) {
		int i = status.getIndex();
		if (i >= this.additionalLevelsByStatus.length) {
			throw new IllegalArgumentException(String.format(Locale.ROOT, "Requesting a ChunkStatus(%s) outside of dependency range(%s)", status, this.dependencies));
		} else {
			return this.additionalLevelsByStatus[i];
		}
	}

	public int getMaxLevel() {
		return Math.max(0, this.dependencies.size() - 1);
	}

	public ChunkStatus get(int index) {
		return (ChunkStatus)this.dependencies.get(index);
	}

	public String toString() {
		return this.dependencies.toString();
	}
}
