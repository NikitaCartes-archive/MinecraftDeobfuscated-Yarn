package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record SimpleBlockFeatureConfig() implements FeatureConfig {
	private final BlockStateProvider toPlace;
	public static final Codec<SimpleBlockFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(BlockStateProvider.TYPE_CODEC.fieldOf("to_place").forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.toPlace))
				.apply(instance, SimpleBlockFeatureConfig::new)
	);

	public SimpleBlockFeatureConfig(BlockStateProvider toPlace) {
		this.toPlace = toPlace;
	}
}
