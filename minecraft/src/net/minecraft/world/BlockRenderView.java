package net.minecraft.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.chunk.light.LightingProvider;

public interface BlockRenderView extends BlockView {
	float getBrightness(Direction direction, boolean shaded);

	LightingProvider getLightingProvider();

	int getColor(BlockPos pos, ColorResolver colorResolver);

	default int getLightLevel(LightType type, BlockPos pos) {
		return this.getLightingProvider().get(type).getLightLevel(pos);
	}

	default int getBaseLightLevel(BlockPos pos, int ambientDarkness) {
		return this.getLightingProvider().getLight(pos, ambientDarkness);
	}

	/**
	 * {@return if the sky is visible at {@code pos}}
	 * 
	 * @implNote This returns {@code true} if the sky light level
	 * at {@code pos} is the maximum, {@code 15}.
	 * 
	 * @see WorldView#isSkyVisibleAllowingSea
	 */
	default boolean isSkyVisible(BlockPos pos) {
		return this.getLightLevel(LightType.SKY, pos) >= this.getMaxLightLevel();
	}
}
