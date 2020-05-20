package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class BasaltColumnsFeatureConfig implements FeatureConfig {
	public static final Codec<BasaltColumnsFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("minimum_reach").forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.minReach),
					Codec.INT.fieldOf("maximum_reach").forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.maxReach),
					Codec.INT.fieldOf("minimum_height").forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.minHeight),
					Codec.INT.fieldOf("maximum_height").forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.maxHeight)
				)
				.apply(instance, BasaltColumnsFeatureConfig::new)
	);
	public final int minReach;
	public final int maxReach;
	public final int minHeight;
	public final int maxHeight;

	public BasaltColumnsFeatureConfig(int minReach, int maxReach, int minHeight, int maxHeight) {
		this.minReach = minReach;
		this.maxReach = maxReach;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
	}

	public static class Builder {
		private int minReach;
		private int maxReach;
		private int minHeight;
		private int maxHeight;

		public BasaltColumnsFeatureConfig.Builder reach(int reach) {
			this.minReach = reach;
			this.maxReach = reach;
			return this;
		}

		public BasaltColumnsFeatureConfig.Builder reach(int min, int max) {
			this.minReach = min;
			this.maxReach = max;
			return this;
		}

		public BasaltColumnsFeatureConfig.Builder height(int min, int max) {
			this.minHeight = min;
			this.maxHeight = max;
			return this;
		}

		public BasaltColumnsFeatureConfig build() {
			if (this.minHeight < 1) {
				throw new IllegalArgumentException("Minimum height cannot be less than 1");
			} else if (this.minReach < 0) {
				throw new IllegalArgumentException("Minimum reach cannot be negative");
			} else if (this.minReach <= this.maxReach && this.minHeight <= this.maxHeight) {
				return new BasaltColumnsFeatureConfig(this.minReach, this.maxReach, this.minHeight, this.maxHeight);
			} else {
				throw new IllegalArgumentException("Minimum reach/height cannot be greater than maximum width/height");
			}
		}
	}
}
