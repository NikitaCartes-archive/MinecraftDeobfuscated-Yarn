package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public interface ExtendedBlockView extends BlockView {
	Biome getBiome(BlockPos blockPos);

	int getLightLevel(LightType lightType, BlockPos blockPos);

	default boolean isSkyVisible(BlockPos blockPos) {
		return this.getLightLevel(LightType.SKY, blockPos) >= this.getMaxLightLevel();
	}

	@Environment(EnvType.CLIENT)
	default int getLightmapIndex(BlockPos blockPos, int i) {
		int j = this.getLightLevel(LightType.SKY, blockPos);
		int k = this.getLightLevel(LightType.BLOCK, blockPos);
		if (k < i) {
			k = i;
		}

		return j << 20 | k << 4;
	}
}
