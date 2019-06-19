package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public interface ExtendedBlockView extends BlockView {
	Biome getBiome(BlockPos blockPos);

	int getLightLevel(LightType lightType, BlockPos blockPos);

	default boolean isSkyVisible(BlockPos blockPos) {
		return this.getLightLevel(LightType.field_9284, blockPos) >= this.getMaxLightLevel();
	}

	@Environment(EnvType.CLIENT)
	default int getLightmapIndex(BlockPos blockPos, int i) {
		int j = this.getLightLevel(LightType.field_9284, blockPos);
		int k = this.getLightLevel(LightType.field_9282, blockPos);
		if (k < i) {
			k = i;
		}

		return j << 20 | k << 4;
	}
}
