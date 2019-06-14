package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
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
		BlockState blockState = BlockTags.field_15461.getRandom(random).method_9564();
		return this.spawnCoral(iWorld, random, blockPos, blockState);
	}

	protected abstract boolean spawnCoral(IWorld iWorld, Random random, BlockPos blockPos, BlockState blockState);

	protected boolean spawnCoralPiece(IWorld iWorld, Random random, BlockPos blockPos, BlockState blockState) {
		BlockPos blockPos2 = blockPos.up();
		BlockState blockState2 = iWorld.method_8320(blockPos);
		if ((blockState2.getBlock() == Blocks.field_10382 || blockState2.matches(BlockTags.field_15488))
			&& iWorld.method_8320(blockPos2).getBlock() == Blocks.field_10382) {
			iWorld.method_8652(blockPos, blockState, 3);
			if (random.nextFloat() < 0.25F) {
				iWorld.method_8652(blockPos2, BlockTags.field_15488.getRandom(random).method_9564(), 2);
			} else if (random.nextFloat() < 0.05F) {
				iWorld.method_8652(blockPos2, Blocks.field_10476.method_9564().method_11657(SeaPickleBlock.field_11472, Integer.valueOf(random.nextInt(4) + 1)), 2);
			}

			for (Direction direction : Direction.Type.field_11062) {
				if (random.nextFloat() < 0.2F) {
					BlockPos blockPos3 = blockPos.offset(direction);
					if (iWorld.method_8320(blockPos3).getBlock() == Blocks.field_10382) {
						BlockState blockState3 = BlockTags.field_15476.getRandom(random).method_9564().method_11657(DeadCoralWallFanBlock.field_9933, direction);
						iWorld.method_8652(blockPos3, blockState3, 2);
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}
}
