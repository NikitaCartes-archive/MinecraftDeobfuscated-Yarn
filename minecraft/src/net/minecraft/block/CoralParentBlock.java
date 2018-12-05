package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class CoralParentBlock extends Block implements Waterloggable {
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	private static final VoxelShape field_9939 = Block.createCubeShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

	protected CoralParentBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(WATERLOGGED, Boolean.valueOf(true)));
	}

	protected void method_9430(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
		if (!method_9431(blockState, iWorld, blockPos)) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 60 + iWorld.getRandom().nextInt(40));
		}
	}

	protected static boolean method_9431(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		if ((Boolean)blockState.get(WATERLOGGED)) {
			return true;
		} else {
			for (Direction direction : Direction.values()) {
				if (blockView.getFluidState(blockPos.method_10093(direction)).matches(FluidTags.field_15517)) {
					return true;
				}
			}

			return false;
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getPos());
		return this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(fluidState.matches(FluidTags.field_15517) && fluidState.method_15761() == 8));
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_9939;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.get(WATERLOGGED)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.method_15789(iWorld));
		}

		return direction == Direction.DOWN && !this.canPlaceAt(blockState, iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		return viewableWorld.getBlockState(blockPos2).hasSolidTopSurface(viewableWorld, blockPos2);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.get(WATERLOGGED) ? Fluids.WATER.getState(false) : super.getFluidState(blockState);
	}
}
