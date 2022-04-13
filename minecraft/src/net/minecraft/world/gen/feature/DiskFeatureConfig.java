package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.stateprovider.PredicatedStateProvider;

public record DiskFeatureConfig(PredicatedStateProvider stateProvider, BlockPredicate target, IntProvider radius, int halfHeight) implements FeatureConfig {
	public static final Codec<DiskFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					PredicatedStateProvider.CODEC.fieldOf("state_provider").forGetter(DiskFeatureConfig::stateProvider),
					BlockPredicate.BASE_CODEC.fieldOf("target").forGetter(DiskFeatureConfig::target),
					IntProvider.createValidatingCodec(0, 8).fieldOf("radius").forGetter(DiskFeatureConfig::radius),
					Codec.intRange(0, 4).fieldOf("half_height").forGetter(DiskFeatureConfig::halfHeight)
				)
				.apply(instance, DiskFeatureConfig::new)
	);
}
