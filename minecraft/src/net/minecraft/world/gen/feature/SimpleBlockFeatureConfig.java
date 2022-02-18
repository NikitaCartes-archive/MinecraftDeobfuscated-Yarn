package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record SimpleBlockFeatureConfig(BlockStateProvider toPlace) implements FeatureConfig {
	public static final Codec<SimpleBlockFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(BlockStateProvider.TYPE_CODEC.fieldOf("to_place").forGetter(config -> config.toPlace))
				.apply(instance, SimpleBlockFeatureConfig::new)
	);
}
