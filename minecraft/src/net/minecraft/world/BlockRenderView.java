package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.level.ColorResolver;

public interface BlockRenderView extends BlockView {
	@Environment(EnvType.CLIENT)
	float getBrightness(Direction direction, boolean shaded);

	LightingProvider getLightingProvider();

	@Environment(EnvType.CLIENT)
	int getColor(BlockPos pos, ColorResolver colorResolver);

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
	Vector3f method_26443(BlockState blockState, BlockPos blockPos);
}
