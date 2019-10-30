package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class PaneBlock extends HorizontalConnectedBlock {
	protected PaneBlock(Block.Settings settings) {
		super(1.0F, 1.0F, 16.0F, 16.0F, 16.0F, settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(NORTH, Boolean.valueOf(false))
				.with(EAST, Boolean.valueOf(false))
				.with(SOUTH, Boolean.valueOf(false))
				.with(WEST, Boolean.valueOf(false))
				.with(WATERLOGGED, Boolean.valueOf(false))
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockView blockView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		BlockPos blockPos2 = blockPos.north();
		BlockPos blockPos3 = blockPos.south();
		BlockPos blockPos4 = blockPos.west();
		BlockPos blockPos5 = blockPos.east();
		BlockState blockState = blockView.getBlockState(blockPos2);
		BlockState blockState2 = blockView.getBlockState(blockPos3);
		BlockState blockState3 = blockView.getBlockState(blockPos4);
		BlockState blockState4 = blockView.getBlockState(blockPos5);
		return this.getDefaultState()
			.with(NORTH, Boolean.valueOf(this.connectsTo(blockState, blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.SOUTH))))
			.with(SOUTH, Boolean.valueOf(this.connectsTo(blockState2, blockState2.isSideSolidFullSquare(blockView, blockPos3, Direction.NORTH))))
			.with(WEST, Boolean.valueOf(this.connectsTo(blockState3, blockState3.isSideSolidFullSquare(blockView, blockPos4, Direction.EAST))))
			.with(EAST, Boolean.valueOf(this.connectsTo(blockState4, blockState4.isSideSolidFullSquare(blockView, blockPos5, Direction.WEST))))
			.with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return facing.getAxis().isHorizontal()
			? state.with(
				(Property)FACING_PROPERTIES.get(facing),
				Boolean.valueOf(this.connectsTo(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, facing.getOpposite())))
			)
			: super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isSideInvisible(BlockState state, BlockState neighbor, Direction facing) {
		if (neighbor.getBlock() == this) {
			if (!facing.getAxis().isHorizontal()) {
				return true;
			}

			if ((Boolean)state.get((Property)FACING_PROPERTIES.get(facing)) && (Boolean)neighbor.get((Property)FACING_PROPERTIES.get(facing.getOpposite()))) {
				return true;
			}
		}

		return super.isSideInvisible(state, neighbor, facing);
	}

	public final boolean connectsTo(BlockState state, boolean bl) {
		Block block = state.getBlock();
		return !canConnect(block) && bl || block instanceof PaneBlock;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}
}
