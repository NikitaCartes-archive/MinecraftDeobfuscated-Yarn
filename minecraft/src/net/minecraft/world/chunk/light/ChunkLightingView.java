package net.minecraft.world.chunk.light;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkNibbleArray;

public interface ChunkLightingView extends LightingView {
	@Nullable
	ChunkNibbleArray getLightArray(ChunkSectionPos chunkSectionPos);

	int getLightLevel(BlockPos blockPos);

	public static enum Empty implements ChunkLightingView {
		INSTANCE;

		@Nullable
		@Override
		public ChunkNibbleArray getLightArray(ChunkSectionPos chunkSectionPos) {
			return null;
		}

		@Override
		public int getLightLevel(BlockPos blockPos) {
			return 0;
		}

		@Override
		public void updateSectionStatus(ChunkSectionPos chunkSectionPos, boolean bl) {
		}
	}
}
