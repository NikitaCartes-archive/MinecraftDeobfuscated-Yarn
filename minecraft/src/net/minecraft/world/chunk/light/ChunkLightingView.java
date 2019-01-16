package net.minecraft.world.chunk.light;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkNibbleArray;

public interface ChunkLightingView extends LightingView {
	@Nullable
	ChunkNibbleArray getChunkLightArray(int i, int j, int k);

	int getLightLevel(BlockPos blockPos);

	public static enum Empty implements ChunkLightingView {
		field_15812;

		@Nullable
		@Override
		public ChunkNibbleArray getChunkLightArray(int i, int j, int k) {
			return null;
		}

		@Override
		public int getLightLevel(BlockPos blockPos) {
			return 0;
		}

		@Override
		public void scheduleChunkLightUpdate(int i, int j, int k, boolean bl) {
		}
	}
}
