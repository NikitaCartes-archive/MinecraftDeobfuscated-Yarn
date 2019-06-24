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

	private static boolean canSurvive(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		if (blockState2.getBlock() == Blocks.SNOW && (Integer)blockState2.get(SnowBlock.LAYERS) == 1) {
			return true;
		} else {
			int i = ChunkLightProvider.method_20049(
				viewableWorld, blockState, blockPos, blockState2, blockPos2, Direction.UP, blockState2.getLightSubtracted(viewableWorld, blockPos2)
			);
			return i < viewableWorld.getMaxLightLevel();
		}
	}

	private static boolean canSpread(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		return canSurvive(blockState, viewableWorld, blockPos) && !viewableWorld.getFluidState(blockPos2).matches(FluidTags.WATER);
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			if (!canSurvive(blockState, world, blockPos)) {
				world.setBlockState(blockPos, Blocks.DIRT.getDefaultState());
			} else if (world.getLightLevel(blockPos.up()) >= 4) {
				if (world.getLightLevel(blockPos.up()) >= 9) {
					BlockState blockState2 = this.getDefaultState();

					for (int i = 0; i < 4; i++) {
						BlockPos blockPos2 = blockPos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
						if (world.getBlockState(blockPos2).getBlock() == Blocks.DIRT && canSpread(blockState2, world, blockPos2)) {
							world.setBlockState(blockPos2, blockState2.with(SNOWY, Boolean.valueOf(world.getBlockState(blockPos2.up()).getBlock() == Blocks.SNOW)));
						}
					}
				}
			}
		}
	}
}
