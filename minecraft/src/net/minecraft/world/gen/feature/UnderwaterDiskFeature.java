package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class UnderwaterDiskFeature extends DiskFeature {
	public UnderwaterDiskFeature(Codec<DiskFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DiskFeatureConfig> context) {
		return !context.getWorld().getFluidState(context.getOrigin()).isIn(FluidTags.WATER) ? false : super.generate(context);
	}
}
