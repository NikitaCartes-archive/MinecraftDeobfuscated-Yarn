package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class NoOpFeature extends Feature<DefaultFeatureConfig> {
	public NoOpFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		return true;
	}
}
