package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.block.BlockState;

public class DeltaFeatureConfig implements FeatureConfig {
	public static final Codec<DeltaFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.CODEC.fieldOf("contents").forGetter(deltaFeatureConfig -> deltaFeatureConfig.contents),
					BlockState.CODEC.fieldOf("rim").forGetter(deltaFeatureConfig -> deltaFeatureConfig.rim),
					Codec.INT.fieldOf("minimum_radius").forGetter(deltaFeatureConfig -> deltaFeatureConfig.minRadius),
					Codec.INT.fieldOf("maximum_radius").forGetter(deltaFeatureConfig -> deltaFeatureConfig.maxRadius),
					Codec.INT.fieldOf("maximum_rim").forGetter(deltaFeatureConfig -> deltaFeatureConfig.maxRim)
				)
				.apply(instance, DeltaFeatureConfig::new)
	);
	public final BlockState contents;
	public final BlockState rim;
	public final int minRadius;
	public final int maxRadius;
	public final int maxRim;

	public DeltaFeatureConfig(BlockState contents, BlockState rim, int minRadius, int maxRadius, int maxRim) {
		this.contents = contents;
		this.rim = rim;
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
		this.maxRim = maxRim;
	}

	public static class Builder {
		Optional<BlockState> contents = Optional.empty();
		Optional<BlockState> rim = Optional.empty();
		int minRadius;
		int maxRadius;
		int maxRim;

		public DeltaFeatureConfig.Builder radius(int min, int max) {
			this.minRadius = min;
			this.maxRadius = max;
			return this;
		}

		public DeltaFeatureConfig.Builder contents(BlockState contents) {
			this.contents = Optional.of(contents);
			return this;
		}

		public DeltaFeatureConfig.Builder rim(BlockState rim, int maxRim) {
			this.rim = Optional.of(rim);
			this.maxRim = maxRim;
			return this;
		}

		public DeltaFeatureConfig build() {
			if (!this.contents.isPresent()) {
				throw new IllegalArgumentException("Missing contents");
			} else if (!this.rim.isPresent()) {
				throw new IllegalArgumentException("Missing rim");
			} else if (this.minRadius > this.maxRadius) {
				throw new IllegalArgumentException("Minimum radius cannot be greater than maximum radius");
			} else {
				return new DeltaFeatureConfig((BlockState)this.contents.get(), (BlockState)this.rim.get(), this.minRadius, this.maxRadius, this.maxRim);
			}
		}
	}
}
