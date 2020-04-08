package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3i;

public class NetherrackReplaceBlobsFeatureConfig implements FeatureConfig {
	public final BlockState target;
	public final BlockState state;
	public final Vec3i minReachPos;
	public final Vec3i maxReachPos;

	public NetherrackReplaceBlobsFeatureConfig(BlockState target, BlockState state, Vec3i minReachPos, Vec3i maxReachPos) {
		this.target = target;
		this.state = state;
		this.minReachPos = minReachPos;
		this.maxReachPos = maxReachPos;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("target"), BlockState.serialize(ops, this.target).getValue());
		builder.put(ops.createString("state"), BlockState.serialize(ops, this.state).getValue());
		builder.put(ops.createString("minimum_reach_x"), ops.createInt(this.minReachPos.getX()));
		builder.put(ops.createString("minimum_reach_y"), ops.createInt(this.minReachPos.getY()));
		builder.put(ops.createString("minimum_reach_z"), ops.createInt(this.minReachPos.getZ()));
		builder.put(ops.createString("maximum_reach_x"), ops.createInt(this.maxReachPos.getX()));
		builder.put(ops.createString("maximum_reach_y"), ops.createInt(this.maxReachPos.getY()));
		builder.put(ops.createString("maximum_reach_z"), ops.createInt(this.maxReachPos.getZ()));
		return new Dynamic<>(ops, ops.createMap(builder.build()));
	}

	public static <T> NetherrackReplaceBlobsFeatureConfig deserialize(Dynamic<T> dynamic) {
		BlockState blockState = (BlockState)dynamic.get("target").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		BlockState blockState2 = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		int i = dynamic.get("minimum_reach_x").asInt(0);
		int j = dynamic.get("minimum_reach_y").asInt(0);
		int k = dynamic.get("minimum_reach_z").asInt(0);
		int l = dynamic.get("maximum_reach_x").asInt(0);
		int m = dynamic.get("maximum_reach_y").asInt(0);
		int n = dynamic.get("maximum_reach_z").asInt(0);
		return new NetherrackReplaceBlobsFeatureConfig(blockState, blockState2, new Vec3i(i, j, k), new Vec3i(l, m, n));
	}

	public static class Builder {
		private BlockState target = Blocks.AIR.getDefaultState();
		private BlockState state = Blocks.AIR.getDefaultState();
		private Vec3i minReachPos = Vec3i.ZERO;
		private Vec3i maxReachPos = Vec3i.ZERO;

		public NetherrackReplaceBlobsFeatureConfig.Builder target(BlockState target) {
			this.target = target;
			return this;
		}

		public NetherrackReplaceBlobsFeatureConfig.Builder state(BlockState state) {
			this.state = state;
			return this;
		}

		public NetherrackReplaceBlobsFeatureConfig.Builder minReachPos(Vec3i minReachPos) {
			this.minReachPos = minReachPos;
			return this;
		}

		public NetherrackReplaceBlobsFeatureConfig.Builder maxReachPos(Vec3i maxReachPos) {
			this.maxReachPos = maxReachPos;
			return this;
		}

		public NetherrackReplaceBlobsFeatureConfig build() {
			if (this.minReachPos.getX() >= 0 && this.minReachPos.getY() >= 0 && this.minReachPos.getZ() >= 0) {
				if (this.minReachPos.getX() <= this.maxReachPos.getX()
					&& this.minReachPos.getY() <= this.maxReachPos.getY()
					&& this.minReachPos.getZ() <= this.maxReachPos.getZ()) {
					return new NetherrackReplaceBlobsFeatureConfig(this.target, this.state, this.minReachPos, this.maxReachPos);
				} else {
					throw new IllegalArgumentException("Maximum reach must be greater than minimum reach for each axis");
				}
			} else {
				throw new IllegalArgumentException("Minimum reach cannot be less than zero");
			}
		}
	}
}
