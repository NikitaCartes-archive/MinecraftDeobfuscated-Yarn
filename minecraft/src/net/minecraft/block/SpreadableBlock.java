package net.minecraft.block;

import java.util.Random;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.ChunkLightProvider;

public abstract class SpreadableBlock extends SnowyBlock {
	protected SpreadableBlock(Block.Settings settings) {
		super(settings);
	}

	private static boolean canSurvive(BlockState state, CollisionView world, BlockPos pos) {
		BlockPos blockPos = pos.up();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == Blocks.SNOW && (Integer)blockState.get(SnowBlock.LAYERS) == 1) {
			return true;
		} else {
			int i = ChunkLightProvider.method_20049(world, state, pos, blockState, blockPos, Direction.UP, blockState.getOpacity(world, blockPos));
			return i < world.getMaxLightLevel();
		}
	}

	private static boolean canSpread(BlockState state, CollisionView world, BlockPos pos) {
		BlockPos blockPos = pos.up();
		return canSurvive(state, world, pos) && !world.getFluidState(blockPos).matches(FluidTags.WATER);
	}

	@Override
	public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!world.isClient) {
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
}
