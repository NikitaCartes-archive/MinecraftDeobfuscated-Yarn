package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4543;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.light.LightingProvider;

public interface ExtendedBlockView extends BlockView {
	class_4543 method_22385();

	LightingProvider method_22336();

	default Biome getBiome(BlockPos blockPos) {
		return this.method_22385().method_22393(blockPos);
	}

	default int getLightLevel(LightType lightType, BlockPos blockPos) {
		return this.method_22336().get(lightType).getLightLevel(blockPos);
	}

	default int method_22335(BlockPos blockPos, int i) {
		return this.method_22336().method_22363(blockPos, i);
	}

	default boolean isSkyVisible(BlockPos blockPos) {
		return this.getLightLevel(LightType.SKY, blockPos) >= this.getMaxLightLevel();
	}

	@Environment(EnvType.CLIENT)
	default int method_22337(BlockPos blockPos) {
		return this.getLightmapIndex(this.getBlockState(blockPos), blockPos);
	}

	@Environment(EnvType.CLIENT)
	default int getLightmapIndex(BlockState blockState, BlockPos blockPos) {
		if (blockState.method_22361()) {
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
