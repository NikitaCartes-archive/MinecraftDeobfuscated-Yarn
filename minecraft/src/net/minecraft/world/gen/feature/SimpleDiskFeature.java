package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SimpleDiskFeature extends DiskFeature {
	public SimpleDiskFeature(Codec<DiskFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DiskFeatureConfig> context) {
		return !context.getWorld().getBlockState(context.getOrigin()).isIn(context.getConfig().canOriginReplace()) ? false : super.generate(context);
	}
}
