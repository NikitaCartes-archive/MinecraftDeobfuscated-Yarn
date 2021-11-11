package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.lang.runtime.ObjectMethods;
import net.minecraft.util.dynamic.Codecs;

public final class TwistingVinesFeatureConfig extends Record implements FeatureConfig {
	private final int spreadWidth;
	private final int spreadHeight;
	private final int maxHeight;
	public static final Codec<TwistingVinesFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.POSITIVE_INT.fieldOf("spread_width").forGetter(TwistingVinesFeatureConfig::spreadWidth),
					Codecs.POSITIVE_INT.fieldOf("spread_height").forGetter(TwistingVinesFeatureConfig::spreadHeight),
					Codecs.POSITIVE_INT.fieldOf("max_height").forGetter(TwistingVinesFeatureConfig::maxHeight)
				)
				.apply(instance, TwistingVinesFeatureConfig::new)
	);

	public TwistingVinesFeatureConfig(int i, int j, int k) {
		this.spreadWidth = i;
		this.spreadHeight = j;
		this.maxHeight = k;
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",TwistingVinesFeatureConfig,"spreadWidth;spreadHeight;maxHeight",TwistingVinesFeatureConfig::spreadWidth,TwistingVinesFeatureConfig::spreadHeight,TwistingVinesFeatureConfig::maxHeight>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",TwistingVinesFeatureConfig,"spreadWidth;spreadHeight;maxHeight",TwistingVinesFeatureConfig::spreadWidth,TwistingVinesFeatureConfig::spreadHeight,TwistingVinesFeatureConfig::maxHeight>(
			this
		);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",TwistingVinesFeatureConfig,"spreadWidth;spreadHeight;maxHeight",TwistingVinesFeatureConfig::spreadWidth,TwistingVinesFeatureConfig::spreadHeight,TwistingVinesFeatureConfig::maxHeight>(
			this, object
		);
	}

	public int spreadWidth() {
		return this.spreadWidth;
	}

	public int spreadHeight() {
		return this.spreadHeight;
	}

	public int maxHeight() {
		return this.maxHeight;
	}
}
