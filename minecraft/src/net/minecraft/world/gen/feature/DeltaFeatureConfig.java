package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class DeltaFeatureConfig implements FeatureConfig {
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

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				new ImmutableMap.Builder<T, T>()
					.put(ops.createString("contents"), BlockState.serialize(ops, this.contents).getValue())
					.put(ops.createString("rim"), BlockState.serialize(ops, this.rim).getValue())
					.put(ops.createString("minimum_radius"), ops.createInt(this.minRadius))
					.put(ops.createString("maximum_radius"), ops.createInt(this.maxRadius))
					.put(ops.createString("maximum_rim"), ops.createInt(this.maxRim))
					.build()
			)
		);
	}

	public static <T> DeltaFeatureConfig deserialize(Dynamic<T> dynamic) {
		BlockState blockState = (BlockState)dynamic.get("contents").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		BlockState blockState2 = (BlockState)dynamic.get("rim").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		int i = dynamic.get("minimum_radius").asInt(0);
		int j = dynamic.get("maximum_radius").asInt(0);
		int k = dynamic.get("maximum_rim").asInt(0);
		return new DeltaFeatureConfig(blockState, blockState2, i, j, k);
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
