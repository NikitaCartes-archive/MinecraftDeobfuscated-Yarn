package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_4538;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class SugarCaneBlock extends Block {
	public static final IntProperty AGE = Properties.AGE_15;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

	protected SugarCaneBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return SHAPE;
	}

	@Override
	public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		if (!blockState.canPlaceAt(serverWorld, blockPos)) {
			serverWorld.breakBlock(blockPos, true);
		} else if (serverWorld.isAir(blockPos.up())) {
			int i = 1;

			while (serverWorld.getBlockState(blockPos.down(i)).getBlock() == this) {
				i++;
			}

			if (i < 3) {
				int j = (Integer)blockState.get(AGE);
				if (j == 15) {
					serverWorld.setBlockState(blockPos.up(), this.getDefaultState());
					serverWorld.setBlockState(blockPos, blockState.with(AGE, Integer.valueOf(0)), 4);
				} else {
					serverWorld.setBlockState(blockPos, blockState.with(AGE, Integer.valueOf(j + 1)), 4);
				}
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (!blockState.canPlaceAt(iWorld, blockPos)) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, class_4538 arg, BlockPos blockPos) {
		Block block = arg.getBlockState(blockPos.method_10074()).getBlock();
		if (block == this) {
			return true;
		} else {
			if (block == Blocks.GRASS_BLOCK
				|| block == Blocks.DIRT
				|| block == Blocks.COARSE_DIRT
				|| block == Blocks.PODZOL
				|| block == Blocks.SAND
				|| block == Blocks.RED_SAND) {
				BlockPos blockPos2 = blockPos.method_10074();

				for (Direction direction : Direction.Type.HORIZONTAL) {
					BlockState blockState2 = arg.getBlockState(blockPos2.offset(direction));
					FluidState fluidState = arg.getFluidState(blockPos2.offset(direction));
					if (fluidState.matches(FluidTags.WATER) || blockState2.getBlock() == Blocks.FROSTED_ICE) {
						return true;
					}
				}
			}

			return false;
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}
}
