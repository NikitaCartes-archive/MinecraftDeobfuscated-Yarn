package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

public class ForestRockFeatureConfig implements FeatureConfig {
	public static final Codec<ForestRockFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.CODEC.fieldOf("state").forGetter(forestRockFeatureConfig -> forestRockFeatureConfig.state),
					Codec.INT.fieldOf("start_radius").withDefault(0).forGetter(forestRockFeatureConfig -> forestRockFeatureConfig.startRadius)
				)
				.apply(instance, ForestRockFeatureConfig::new)
	);
	public final BlockState state;
	public final int startRadius;

	public ForestRockFeatureConfig(BlockState state, int startRadius) {
		this.state = state;
		this.startRadius = startRadius;
	}
}
