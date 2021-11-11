package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.lang.runtime.ObjectMethods;
import java.util.function.Supplier;
import net.minecraft.util.dynamic.Codecs;

public final class RandomPatchFeatureConfig extends Record implements FeatureConfig {
	private final int tries;
	private final int xzSpread;
	private final int ySpread;
	private final Supplier<PlacedFeature> feature;
	public static final Codec<RandomPatchFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.POSITIVE_INT.fieldOf("tries").orElse(128).forGetter(RandomPatchFeatureConfig::tries),
					Codecs.NONNEGATIVE_INT.fieldOf("xz_spread").orElse(7).forGetter(RandomPatchFeatureConfig::xzSpread),
					Codecs.NONNEGATIVE_INT.fieldOf("y_spread").orElse(3).forGetter(RandomPatchFeatureConfig::ySpread),
					PlacedFeature.REGISTRY_CODEC.fieldOf("feature").forGetter(RandomPatchFeatureConfig::feature)
				)
				.apply(instance, RandomPatchFeatureConfig::new)
	);

	public RandomPatchFeatureConfig(int i, int j, int k, Supplier<PlacedFeature> supplier) {
		this.tries = i;
		this.xzSpread = j;
		this.ySpread = k;
		this.feature = supplier;
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",RandomPatchFeatureConfig,"tries;xzSpread;ySpread;feature",RandomPatchFeatureConfig::tries,RandomPatchFeatureConfig::xzSpread,RandomPatchFeatureConfig::ySpread,RandomPatchFeatureConfig::feature>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",RandomPatchFeatureConfig,"tries;xzSpread;ySpread;feature",RandomPatchFeatureConfig::tries,RandomPatchFeatureConfig::xzSpread,RandomPatchFeatureConfig::ySpread,RandomPatchFeatureConfig::feature>(
			this
		);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",RandomPatchFeatureConfig,"tries;xzSpread;ySpread;feature",RandomPatchFeatureConfig::tries,RandomPatchFeatureConfig::xzSpread,RandomPatchFeatureConfig::ySpread,RandomPatchFeatureConfig::feature>(
			this, object
		);
	}

	public int tries() {
		return this.tries;
	}

	public int xzSpread() {
		return this.xzSpread;
	}

	public int ySpread() {
		return this.ySpread;
	}

	public Supplier<PlacedFeature> feature() {
		return this.feature;
	}
}
