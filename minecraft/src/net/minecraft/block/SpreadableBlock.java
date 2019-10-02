package net.minecraft.block;

import java.util.Random;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;

public abstract class SpreadableBlock extends SnowyBlock {
	protected SpreadableBlock(Block.Settings settings) {
		super(settings);
	}

	private static boolean canSurvive(BlockState blockState, WorldView worldView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		BlockState blockState2 = worldView.getBlockState(blockPos2);
		if (blockState2.getBlock() == Blocks.SNOW && (Integer)blockState2.get(SnowBlock.LAYERS) == 1) {
			return true;
		} else {
			int i = ChunkLightProvider.getRealisticOpacity(
				worldView, blockState, blockPos, blockState2, blockPos2, Direction.UP, blockState2.getOpacity(worldView, blockPos2)
			);
			return i < worldView.getMaxLightLevel();
		}
	}

	private static boolean canSpread(BlockState blockState, WorldView worldView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		return canSurvive(blockState, worldView, blockPos) && !worldView.getFluidState(blockPos2).matches(FluidTags.WATER);
	}

	@Override
	public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		if (!canSurvive(blockState, serverWorld, blockPos)) {
			serverWorld.setBlockState(blockPos, Blocks.DIRT.getDefaultState());
		} else {
			if (serverWorld.getLightLevel(blockPos.up()) >= 9) {
				BlockState blockState2 = this.getDefaultState();

				for (int i = 0; i < 4; i++) {
					BlockPos blockPos2 = blockPos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
					if (serverWorld.getBlockState(blockPos2).getBlock() == Blocks.DIRT && canSpread(blockState2, serverWorld, blockPos2)) {
						serverWorld.setBlockState(blockPos2, blockState2.with(SNOWY, Boolean.valueOf(serverWorld.getBlockState(blockPos2.up()).getBlock() == Blocks.SNOW)));
					}
				}
			}
		}
	}
}
