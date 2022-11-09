package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;

public record RandomPatchFeatureConfig(int tries, int xzSpread, int ySpread, RegistryEntry<PlacedFeature> feature) implements FeatureConfig {
	public static final Codec<RandomPatchFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.POSITIVE_INT.fieldOf("tries").orElse(128).forGetter(RandomPatchFeatureConfig::tries),
					Codecs.NONNEGATIVE_INT.fieldOf("xz_spread").orElse(7).forGetter(RandomPatchFeatureConfig::xzSpread),
					Codecs.NONNEGATIVE_INT.fieldOf("y_spread").orElse(3).forGetter(RandomPatchFeatureConfig::ySpread),
					PlacedFeature.REGISTRY_CODEC.fieldOf("feature").forGetter(RandomPatchFeatureConfig::feature)
				)
				.apply(instance, RandomPatchFeatureConfig::new)
	);
}
