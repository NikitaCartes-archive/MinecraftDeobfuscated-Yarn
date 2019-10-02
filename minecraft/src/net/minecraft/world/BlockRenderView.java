package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAccess;
import net.minecraft.world.chunk.light.LightingProvider;

public interface BlockRenderView extends BlockView {
	BiomeAccess getBiomeAccess();

	LightingProvider getLightingProvider();

	default Biome getBiome(BlockPos blockPos) {
		return this.getBiomeAccess().getBiome(blockPos);
	}

	default int getLightLevel(LightType lightType, BlockPos blockPos) {
		return this.getLightingProvider().get(lightType).getLightLevel(blockPos);
	}

	default int getBaseLightLevel(BlockPos blockPos, int i) {
		return this.getLightingProvider().getLight(blockPos, i);
	}

	default boolean isSkyVisible(BlockPos blockPos) {
		return this.getLightLevel(LightType.SKY, blockPos) >= this.getMaxLightLevel();
	}

	@Environment(EnvType.CLIENT)
	default int getLightmapCoordinates(BlockPos blockPos) {
		return this.getLightmapCoordinates(this.getBlockState(blockPos), blockPos);
	}

	@Environment(EnvType.CLIENT)
	default int getLightmapCoordinates(BlockState blockState, BlockPos blockPos) {
		if (blockState.hasEmissiveLighting()) {
			return 15728880;
		} else {
			int i = this.getLightLevel(LightType.SKY, blockPos);
			int j = this.getLightLevel(LightType.BLOCK, blockPos);
			int k = blockState.getLuminance();
			if (j < k) {
				j = k;
			}

			return i << 20 | j << 4;
		}
	}
}
