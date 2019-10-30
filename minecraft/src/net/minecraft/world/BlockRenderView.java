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

	default Biome getBiome(BlockPos pos) {
		return this.getBiomeAccess().getBiome(pos);
	}

	default int getLightLevel(LightType type, BlockPos pos) {
		return this.getLightingProvider().get(type).getLightLevel(pos);
	}

	default int getBaseLightLevel(BlockPos pos, int ambientDarkness) {
		return this.getLightingProvider().getLight(pos, ambientDarkness);
	}

	default boolean isSkyVisible(BlockPos pos) {
		return this.getLightLevel(LightType.SKY, pos) >= this.getMaxLightLevel();
	}

	@Environment(EnvType.CLIENT)
	default int getLightmapCoordinates(BlockPos pos) {
		return this.getLightmapCoordinates(this.getBlockState(pos), pos);
	}

	@Environment(EnvType.CLIENT)
	default int getLightmapCoordinates(BlockState state, BlockPos pos) {
		if (state.hasEmissiveLighting()) {
			return 15728880;
		} else {
			int i = this.getLightLevel(LightType.SKY, pos);
			int j = this.getLightLevel(LightType.BLOCK, pos);
			int k = state.getLuminance();
			if (j < k) {
				j = k;
			}

			return i << 20 | j << 4;
		}
	}
}
