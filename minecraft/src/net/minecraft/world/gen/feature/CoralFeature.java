package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public abstract class CoralFeature extends Feature<DefaultFeatureConfig> {
	public CoralFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		BlockState blockState = BlockTags.CORAL_BLOCKS.getRandom(random).getDefaultState();
		return this.spawnCoral(structureWorldAccess, random, blockPos, blockState);
	}

	protected abstract boolean spawnCoral(WorldAccess world, Random random, BlockPos pos, BlockState state);

	protected boolean spawnCoralPiece(WorldAccess world, Random random, BlockPos pos, BlockState state) {
		BlockPos blockPos = pos.up();
		BlockState blockState = world.getBlockState(pos);
		if ((blockState.isOf(Blocks.WATER) || blockState.isIn(BlockTags.CORALS)) && world.getBlockState(blockPos).isOf(Blocks.WATER)) {
			world.setBlockState(pos, state, 3);
			if (random.nextFloat() < 0.25F) {
				world.setBlockState(blockPos, BlockTags.CORALS.getRandom(random).getDefaultState(), 2);
			} else if (random.nextFloat() < 0.05F) {
				world.setBlockState(blockPos, Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, Integer.valueOf(random.nextInt(4) + 1)), 2);
			}

			for (Direction direction : Direction.Type.HORIZONTAL) {
				if (random.nextFloat() < 0.2F) {
					BlockPos blockPos2 = pos.offset(direction);
					if (world.getBlockState(blockPos2).isOf(Blocks.WATER)) {
						BlockState blockState2 = BlockTags.WALL_CORALS.getRandom(random).getDefaultState().with(DeadCoralWallFanBlock.FACING, direction);
						world.setBlockState(blockPos2, blockState2, 2);
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}
}
