package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.intprovider.IntProvider;

public class BasaltColumnsFeatureConfig implements FeatureConfig {
	public static final Codec<BasaltColumnsFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.CODEC.fieldOf("state").forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.field_51010),
					IntProvider.createValidatingCodec(0, 3).fieldOf("reach").forGetter(config -> config.reach),
					IntProvider.createValidatingCodec(1, 20).fieldOf("height").forGetter(config -> config.height)
				)
				.apply(instance, BasaltColumnsFeatureConfig::new)
	);
	private final IntProvider reach;
	private final IntProvider height;
	private final BlockState field_51010;

	public BasaltColumnsFeatureConfig(BlockState blockState, IntProvider intProvider, IntProvider intProvider2) {
		this.reach = intProvider;
		this.height = intProvider2;
		this.field_51010 = blockState;
	}

	public IntProvider getReach() {
		return this.reach;
	}

	public IntProvider getHeight() {
		return this.height;
	}

	public BlockState method_59277() {
		return this.field_51010;
	}
}
