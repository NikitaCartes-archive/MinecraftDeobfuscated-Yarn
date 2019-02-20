package net.minecraft.world.chunk.light;

import javax.annotation.Nullable;
import net.minecraft.class_4076;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkNibbleArray;

public interface ChunkLightingView extends LightingView {
	@Nullable
	ChunkNibbleArray getChunkLightArray(class_4076 arg);

	int getLightLevel(BlockPos blockPos);

	public static enum Empty implements ChunkLightingView {
		field_15812;

		@Nullable
		@Override
		public ChunkNibbleArray getChunkLightArray(class_4076 arg) {
			return null;
		}

		@Override
		public int getLightLevel(BlockPos blockPos) {
			return 0;
		}

		@Override
		public void scheduleChunkLightUpdate(class_4076 arg, boolean bl) {
		}
	}
}
