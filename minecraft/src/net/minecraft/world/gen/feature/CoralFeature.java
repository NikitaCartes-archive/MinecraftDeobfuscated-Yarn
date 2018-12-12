package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CoralDeadWallFanBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public abstract class CoralFeature extends Feature<DefaultFeatureConfig> {
	public CoralFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_12865(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		BlockState blockState = BlockTags.field_15461.getRandom(random).getDefaultState();
		return this.method_12863(iWorld, random, blockPos, blockState);
	}

	protected abstract boolean method_12863(IWorld iWorld, Random random, BlockPos blockPos, BlockState blockState);

	protected boolean method_12864(IWorld iWorld, Random random, BlockPos blockPos, BlockState blockState) {
		BlockPos blockPos2 = blockPos.up();
		BlockState blockState2 = iWorld.getBlockState(blockPos);
		if ((blockState2.getBlock() == Blocks.field_10382 || blockState2.matches(BlockTags.field_15488))
			&& iWorld.getBlockState(blockPos2).getBlock() == Blocks.field_10382) {
			iWorld.setBlockState(blockPos, blockState, 3);
			if (random.nextFloat() < 0.25F) {
				iWorld.setBlockState(blockPos2, BlockTags.field_15488.getRandom(random).getDefaultState(), 2);
			} else if (random.nextFloat() < 0.05F) {
				iWorld.setBlockState(blockPos2, Blocks.field_10476.getDefaultState().with(SeaPickleBlock.field_11472, Integer.valueOf(random.nextInt(4) + 1)), 2);
			}

			for (Direction direction : Direction.class_2353.HORIZONTAL) {
				if (random.nextFloat() < 0.2F) {
					BlockPos blockPos3 = blockPos.offset(direction);
					if (iWorld.getBlockState(blockPos3).getBlock() == Blocks.field_10382) {
						BlockState blockState3 = BlockTags.field_15476.getRandom(random).getDefaultState().with(CoralDeadWallFanBlock.field_9933, direction);
						iWorld.setBlockState(blockPos3, blockState3, 2);
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}
}
