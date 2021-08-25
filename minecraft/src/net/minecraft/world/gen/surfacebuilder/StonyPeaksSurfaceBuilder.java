package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;

public class StonyPeaksSurfaceBuilder extends StoneSurfaceBuilder {
	private static final float field_34318 = 0.025F;

	public StonyPeaksSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	@Override
	protected TernarySurfaceConfig getLayerBlockConfig(double noise) {
		TernarySurfaceConfig ternarySurfaceConfig;
		if (noise < -0.025F) {
			ternarySurfaceConfig = SurfaceBuilder.CACLCITE_CONFIG;
		} else if (noise < 0.0) {
			ternarySurfaceConfig = SurfaceBuilder.ANDESITE_CONFIG;
		} else if (noise < 0.025F) {
			ternarySurfaceConfig = SurfaceBuilder.GRAVEL_CONFIG;
		} else {
			ternarySurfaceConfig = SurfaceBuilder.GRANITE_CONFIG;
		}

		return ternarySurfaceConfig;
	}
}
