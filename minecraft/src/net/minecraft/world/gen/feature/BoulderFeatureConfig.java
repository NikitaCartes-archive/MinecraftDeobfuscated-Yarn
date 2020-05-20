package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

public class BoulderFeatureConfig implements FeatureConfig {
	public static final Codec<BoulderFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.field_24734.fieldOf("state").forGetter(boulderFeatureConfig -> boulderFeatureConfig.state),
					Codec.INT.fieldOf("start_radius").withDefault(0).forGetter(boulderFeatureConfig -> boulderFeatureConfig.startRadius)
				)
				.apply(instance, BoulderFeatureConfig::new)
	);
	public final BlockState state;
	public final int startRadius;

	public BoulderFeatureConfig(BlockState state, int startRadius) {
		this.state = state;
		this.startRadius = startRadius;
	}
}
