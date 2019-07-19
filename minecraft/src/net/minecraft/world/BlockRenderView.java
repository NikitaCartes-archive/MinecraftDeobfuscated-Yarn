package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public interface BlockRenderView extends BlockView {
	Biome getBiome(BlockPos blockPos);

	int getLightLevel(LightType type, BlockPos pos);

	default boolean isSkyVisible(BlockPos pos) {
		return this.getLightLevel(LightType.SKY, pos) >= this.getMaxLightLevel();
	}

	@Environment(EnvType.CLIENT)
	default int getLightmapIndex(BlockPos pos, int i) {
		int j = this.getLightLevel(LightType.SKY, pos);
		int k = this.getLightLevel(LightType.BLOCK, pos);
		if (k < i) {
			k = i;
		}

		return j << 20 | k << 4;
	}
}
