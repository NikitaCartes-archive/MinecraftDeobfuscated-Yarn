package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class UnderwaterDiskFeature extends DiskFeature {
	public UnderwaterDiskFeature(Codec<DiskFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DiskFeatureConfig> featureContext) {
		return !featureContext.getWorld().getFluidState(featureContext.getPos()).isIn(FluidTags.WATER) ? false : super.generate(featureContext);
	}
}
