package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3i;

public class NetherrackReplaceBlobsFeatureConfig implements FeatureConfig {
	public static final Codec<NetherrackReplaceBlobsFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.CODEC.fieldOf("target").forGetter(netherrackReplaceBlobsFeatureConfig -> netherrackReplaceBlobsFeatureConfig.target),
					BlockState.CODEC.fieldOf("state").forGetter(netherrackReplaceBlobsFeatureConfig -> netherrackReplaceBlobsFeatureConfig.state),
					Vec3i.field_25123.fieldOf("minimum_reach").forGetter(netherrackReplaceBlobsFeatureConfig -> netherrackReplaceBlobsFeatureConfig.minReachPos),
					Vec3i.field_25123.fieldOf("maximum_reach").forGetter(netherrackReplaceBlobsFeatureConfig -> netherrackReplaceBlobsFeatureConfig.maxReachPos)
				)
				.apply(instance, NetherrackReplaceBlobsFeatureConfig::new)
	);
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
