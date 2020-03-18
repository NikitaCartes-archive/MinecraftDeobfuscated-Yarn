package net.minecraft.block;

import java.util.Random;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;

public abstract class SpreadableBlock extends SnowyBlock {
	protected SpreadableBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	private static boolean canSurvive(BlockState state, WorldView worldView, BlockPos pos) {
		BlockPos blockPos = pos.up();
		BlockState blockState = worldView.getBlockState(blockPos);
		if (blockState.getBlock() == Blocks.SNOW && (Integer)blockState.get(SnowBlock.LAYERS) == 1) {
			return true;
		} else if (blockState.getFluidState().getFluid() != Fluids.EMPTY) {
			return false;
		} else {
			int i = ChunkLightProvider.getRealisticOpacity(worldView, state, pos, blockState, blockPos, Direction.UP, blockState.getOpacity(worldView, blockPos));
			return i < worldView.getMaxLightLevel();
		}
	}

	private static boolean canSpread(BlockState state, WorldView worldView, BlockPos pos) {
		BlockPos blockPos = pos.up();
		return canSurvive(state, worldView, pos) && !worldView.getFluidState(blockPos).matches(FluidTags.WATER);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!canSurvive(state, world, pos)) {
			world.setBlockState(pos, Blocks.DIRT.getDefaultState());
		} else {
			if (world.getLightLevel(pos.up()) >= 9) {
				BlockState blockState = this.getDefaultState();

				for (int i = 0; i < 4; i++) {
					BlockPos blockPos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
					if (world.getBlockState(blockPos).getBlock() == Blocks.DIRT && canSpread(blockState, world, blockPos)) {
						world.setBlockState(blockPos, blockState.with(SNOWY, Boolean.valueOf(world.getBlockState(blockPos.up()).getBlock() == Blocks.SNOW)));
					}
				}
			}
		}
	}
}
