package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.registry.Registry;

public record RandomPatchFeatureConfig() implements FeatureConfig {
	private final int tries;
	private final int xzSpread;
	private final int ySpread;
	private final Set<Block> allowedOn;
	private final Set<BlockState> disallowedOn;
	private final boolean onlyInAir;
	private final Supplier<ConfiguredFeature<?, ?>> feature;
	public static final Codec<RandomPatchFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.POSITIVE_INT.fieldOf("tries").orElse(128).forGetter(RandomPatchFeatureConfig::tries),
					Codecs.NONNEGATIVE_INT.fieldOf("xz_spread").orElse(7).forGetter(RandomPatchFeatureConfig::xzSpread),
					Codecs.NONNEGATIVE_INT.fieldOf("y_spread").orElse(3).forGetter(RandomPatchFeatureConfig::ySpread),
					Registry.BLOCK.listOf().xmap(Set::copyOf, List::copyOf).fieldOf("allowed_on").forGetter(RandomPatchFeatureConfig::allowedOn),
					BlockState.CODEC.listOf().xmap(Set::copyOf, List::copyOf).fieldOf("disallowed_on").forGetter(RandomPatchFeatureConfig::disallowedOn),
					Codec.BOOL.fieldOf("only_in_air").forGetter(RandomPatchFeatureConfig::onlyInAir),
					ConfiguredFeature.REGISTRY_CODEC.fieldOf("feature").forGetter(RandomPatchFeatureConfig::feature)
				)
				.apply(instance, RandomPatchFeatureConfig::new)
	);

	public RandomPatchFeatureConfig(int i, int j, int k, Set<Block> blacklist, Set<BlockState> set, boolean bl, Supplier<ConfiguredFeature<?, ?>> supplier) {
		this.tries = i;
		this.xzSpread = j;
		this.ySpread = k;
		this.allowedOn = blacklist;
		this.disallowedOn = set;
		this.onlyInAir = bl;
		this.feature = supplier;
	}
}
