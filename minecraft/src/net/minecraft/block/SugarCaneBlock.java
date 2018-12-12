package net.minecraft.block;

import java.util.Random;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class SugarCaneBlock extends Block {
	public static final IntegerProperty AGE = Properties.AGE_15;
	protected static final VoxelShape field_11611 = Block.createCubeShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

	protected SugarCaneBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_11611;
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (blockState.canPlaceAt(world, blockPos) && world.isAir(blockPos.up())) {
			int i = 1;

			while (world.getBlockState(blockPos.down(i)).getBlock() == this) {
				i++;
			}

			if (i < 3) {
				int j = (Integer)blockState.get(AGE);
				if (j == 15) {
					world.setBlockState(blockPos.up(), this.getDefaultState());
					world.setBlockState(blockPos, blockState.with(AGE, Integer.valueOf(0)), 4);
				} else {
					world.setBlockState(blockPos, blockState.with(AGE, Integer.valueOf(j + 1)), 4);
				}
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Block block = viewableWorld.getBlockState(blockPos.down()).getBlock();
		if (block == this) {
			return true;
		} else {
			if (block == Blocks.field_10219
				|| block == Blocks.field_10566
				|| block == Blocks.field_10253
				|| block == Blocks.field_10520
				|| block == Blocks.field_10102
				|| block == Blocks.field_10534) {
				BlockPos blockPos2 = blockPos.down();

				for (Direction direction : Direction.class_2353.HORIZONTAL) {
					BlockState blockState2 = viewableWorld.getBlockState(blockPos2.offset(direction));
					FluidState fluidState = viewableWorld.getFluidState(blockPos2.offset(direction));
					if (fluidState.matches(FluidTags.field_15517) || blockState2.getBlock() == Blocks.field_10110) {
						return true;
					}
				}
			}

			return false;
		}
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(AGE);
	}
}
