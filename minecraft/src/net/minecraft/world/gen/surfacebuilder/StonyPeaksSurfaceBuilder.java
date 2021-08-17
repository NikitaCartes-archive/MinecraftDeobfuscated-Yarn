package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import net.minecraft.class_6485;

public class StonyPeaksSurfaceBuilder extends class_6485 {
	private static final float field_34318 = 0.025F;

	public StonyPeaksSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	@Override
	protected TernarySurfaceConfig method_37866(double d) {
		TernarySurfaceConfig ternarySurfaceConfig;
		if (d < -0.025F) {
			ternarySurfaceConfig = SurfaceBuilder.field_34332;
		} else if (d < 0.0) {
			ternarySurfaceConfig = SurfaceBuilder.field_34330;
		} else if (d < 0.025F) {
			ternarySurfaceConfig = SurfaceBuilder.GRAVEL_CONFIG;
		} else {
			ternarySurfaceConfig = SurfaceBuilder.field_34329;
		}

		return ternarySurfaceConfig;
	}
}
