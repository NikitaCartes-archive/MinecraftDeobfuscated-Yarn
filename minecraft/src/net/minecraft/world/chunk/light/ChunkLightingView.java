package net.minecraft.world.chunk.light;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkNibbleArray;

public interface ChunkLightingView extends LightingView {
	@Nullable
	ChunkNibbleArray method_15544(ChunkSectionPos chunkSectionPos);

	int method_15543(BlockPos blockPos);

	public static enum Empty implements ChunkLightingView {
		field_15812;

		@Nullable
		@Override
		public ChunkNibbleArray method_15544(ChunkSectionPos chunkSectionPos) {
			return null;
		}

		@Override
		public int method_15543(BlockPos blockPos) {
			return 0;
		}

		@Override
		public void method_15551(ChunkSectionPos chunkSectionPos, boolean bl) {
		}
	}
}
