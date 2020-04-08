package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class NetherrackReplaceBlobsFeature extends Feature<NetherrackReplaceBlobsFeatureConfig> {
	public NetherrackReplaceBlobsFeature(Function<Dynamic<?>, ? extends NetherrackReplaceBlobsFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		IWorld iWorld,
		StructureAccessor structureAccessor,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		BlockPos blockPos,
		NetherrackReplaceBlobsFeatureConfig netherrackReplaceBlobsFeatureConfig
	) {
		Block block = netherrackReplaceBlobsFeatureConfig.target.getBlock();
		BlockPos blockPos2 = method_27107(iWorld, blockPos.mutableCopy().method_27158(Direction.Axis.Y, 1, iWorld.getHeight() - 1), block);
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

				BlockState blockState = iWorld.getBlockState(blockPos3);
				if (blockState.getBlock() == block) {
					this.setBlockState(iWorld, blockPos3, netherrackReplaceBlobsFeatureConfig.state);
					bl = true;
				}
			}

			return bl;
		}
	}

	@Nullable
	private static BlockPos method_27107(IWorld iWorld, BlockPos.Mutable mutable, Block block) {
		while (mutable.getY() > 1) {
			BlockState blockState = iWorld.getBlockState(mutable);
			if (blockState.getBlock() == block) {
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
