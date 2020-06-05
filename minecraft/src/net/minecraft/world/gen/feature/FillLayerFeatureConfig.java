package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

public class FillLayerFeatureConfig implements FeatureConfig {
	public static final Codec<FillLayerFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("height").forGetter(fillLayerFeatureConfig -> fillLayerFeatureConfig.height),
					BlockState.CODEC.fieldOf("state").forGetter(fillLayerFeatureConfig -> fillLayerFeatureConfig.state)
				)
				.apply(instance, FillLayerFeatureConfig::new)
	);
	public final int height;
	public final BlockState state;

	public FillLayerFeatureConfig(int height, BlockState state) {
		this.height = height;
		this.state = state;
	}
}
