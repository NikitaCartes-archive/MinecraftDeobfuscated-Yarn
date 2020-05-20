package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class NetherrackReplaceBlobsFeature extends Feature<NetherrackReplaceBlobsFeatureConfig> {
	public NetherrackReplaceBlobsFeature(Codec<NetherrackReplaceBlobsFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		NetherrackReplaceBlobsFeatureConfig netherrackReplaceBlobsFeatureConfig
	) {
		Block block = netherrackReplaceBlobsFeatureConfig.target.getBlock();
		BlockPos blockPos2 = method_27107(serverWorldAccess, blockPos.mutableCopy().method_27158(Direction.Axis.Y, 1, serverWorldAccess.getHeight() - 1), block);
		if (blockPos2 == null) {
			return false;
		} else {
			Vec3i vec3i = method_27108(random, netherrackReplaceBlobsFeatureConfig);
			int i = Math.max(vec3i.getX(), Math.max(vec3i.getY(), vec3i.getZ()));
			boolean bl = false;

			for (BlockPos blockPos3 : BlockPos.iterateOutwards(blockPos2, vec3i.getX(), vec3i.getY(), vec3i.getZ())) {
				if (blockPos3.getManhattanDistance(blockPos2) > i) {
					break;
				}

				BlockState blockState = serverWorldAccess.getBlockState(blockPos3);
				if (blockState.isOf(block)) {
					this.setBlockState(serverWorldAccess, blockPos3, netherrackReplaceBlobsFeatureConfig.state);
					bl = true;
				}
			}

			return bl;
		}
	}

	@Nullable
	private static BlockPos method_27107(WorldAccess worldAccess, BlockPos.Mutable mutable, Block block) {
		while (mutable.getY() > 1) {
			BlockState blockState = worldAccess.getBlockState(mutable);
			if (blockState.isOf(block)) {
				return mutable;
			}

			mutable.move(Direction.DOWN);
		}

		return null;
	}

	private static Vec3i method_27108(Random random, NetherrackReplaceBlobsFeatureConfig netherrackReplaceBlobsFeatureConfig) {
		return new Vec3i(
			netherrackReplaceBlobsFeatureConfig.minReachPos.getX()
				+ random.nextInt(netherrackReplaceBlobsFeatureConfig.maxReachPos.getX() - netherrackReplaceBlobsFeatureConfig.minReachPos.getX() + 1),
			netherrackReplaceBlobsFeatureConfig.minReachPos.getY()
				+ random.nextInt(netherrackReplaceBlobsFeatureConfig.maxReachPos.getY() - netherrackReplaceBlobsFeatureConfig.minReachPos.getY() + 1),
			netherrackReplaceBlobsFeatureConfig.minReachPos.getZ()
				+ random.nextInt(netherrackReplaceBlobsFeatureConfig.maxReachPos.getZ() - netherrackReplaceBlobsFeatureConfig.minReachPos.getZ() + 1)
		);
	}
}
