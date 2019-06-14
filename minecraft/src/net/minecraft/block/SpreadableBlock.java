package net.minecraft.block;

import java.util.Random;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.ChunkLightProvider;

public abstract class SpreadableBlock extends SnowyBlock {
	protected SpreadableBlock(Block.Settings settings) {
		super(settings);
	}

	private static boolean method_10614(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		BlockState blockState2 = viewableWorld.method_8320(blockPos2);
		if (blockState2.getBlock() == Blocks.field_10477 && (Integer)blockState2.method_11654(SnowBlock.field_11518) == 1) {
			return true;
		} else {
			int i = ChunkLightProvider.method_20049(
				viewableWorld, blockState, blockPos, blockState2, blockPos2, Direction.field_11036, blockState2.getLightSubtracted(viewableWorld, blockPos2)
			);
			return i < viewableWorld.getMaxLightLevel();
		}
	}

	private static boolean method_10613(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		return method_10614(blockState, viewableWorld, blockPos) && !viewableWorld.method_8316(blockPos2).matches(FluidTags.field_15517);
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			if (!method_10614(blockState, world, blockPos)) {
				world.method_8501(blockPos, Blocks.field_10566.method_9564());
			} else if (world.getLightLevel(blockPos.up()) >= 4) {
				if (world.getLightLevel(blockPos.up()) >= 9) {
					BlockState blockState2 = this.method_9564();

					for (int i = 0; i < 4; i++) {
						BlockPos blockPos2 = blockPos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
						if (world.method_8320(blockPos2).getBlock() == Blocks.field_10566 && method_10613(blockState2, world, blockPos2)) {
							world.method_8501(blockPos2, blockState2.method_11657(field_11522, Boolean.valueOf(world.method_8320(blockPos2.up()).getBlock() == Blocks.field_10477)));
						}
					}
				}
			}
		}
	}
}
