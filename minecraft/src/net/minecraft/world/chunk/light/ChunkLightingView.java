package net.minecraft.world.chunk.light;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkNibbleArray;

public interface ChunkLightingView extends LightingView {
	@Nullable
	ChunkNibbleArray getLightSection(ChunkSectionPos pos);

	int getLightLevel(BlockPos pos);

	public static enum Empty implements ChunkLightingView {
		INSTANCE;

		@Nullable
		@Override
		public ChunkNibbleArray getLightSection(ChunkSectionPos pos) {
			return null;
		}

		@Override
		public int getLightLevel(BlockPos pos) {
			return 0;
		}

		@Override
		public void checkBlock(BlockPos pos) {
		}

		@Override
		public void addLightSource(BlockPos pos, int level) {
		}

		@Override
		public boolean hasUpdates() {
			return false;
		}

		@Override
		public int doLightUpdates(int i, boolean bl, boolean bl2) {
			return i;
		}

		@Override
		public void setSectionStatus(ChunkSectionPos pos, boolean notReady) {
		}

		@Override
		public void setColumnEnabled(ChunkPos chunkPos, boolean bl) {
		}
	}
}
