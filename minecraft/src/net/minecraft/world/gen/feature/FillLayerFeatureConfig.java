package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.dimension.DimensionType;

public class FillLayerFeatureConfig implements FeatureConfig {
	public static final Codec<FillLayerFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.intRange(0, DimensionType.MAX_HEIGHT).fieldOf("height").forGetter(config -> config.height),
					BlockState.CODEC.fieldOf("state").forGetter(config -> config.state)
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
