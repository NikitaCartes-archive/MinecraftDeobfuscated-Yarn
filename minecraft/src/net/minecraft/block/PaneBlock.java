package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
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
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getBlockPos());
		BlockPos blockPos2 = blockPos.north();
		BlockPos blockPos3 = blockPos.south();
		BlockPos blockPos4 = blockPos.west();
		BlockPos blockPos5 = blockPos.east();
		BlockState blockState = blockView.getBlockState(blockPos2);
		BlockState blockState2 = blockView.getBlockState(blockPos3);
		BlockState blockState3 = blockView.getBlockState(blockPos4);
		BlockState blockState4 = blockView.getBlockState(blockPos5);
		return this.getDefaultState()
			.with(NORTH, Boolean.valueOf(this.connectsTo(blockState, Block.isSolidFullSquare(blockState, blockView, blockPos2, Direction.field_11035))))
			.with(SOUTH, Boolean.valueOf(this.connectsTo(blockState2, Block.isSolidFullSquare(blockState2, blockView, blockPos3, Direction.field_11043))))
			.with(WEST, Boolean.valueOf(this.connectsTo(blockState3, Block.isSolidFullSquare(blockState3, blockView, blockPos4, Direction.field_11034))))
			.with(EAST, Boolean.valueOf(this.connectsTo(blockState4, Block.isSolidFullSquare(blockState4, blockView, blockPos5, Direction.field_11039))))
			.with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if ((Boolean)blockState.get(WATERLOGGED)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return direction.getAxis().isHorizontal()
			? blockState.with(
				(Property)FACING_PROPERTIES.get(direction),
				Boolean.valueOf(this.connectsTo(blockState2, Block.isSolidFullSquare(blockState2, iWorld, blockPos2, direction.getOpposite())))
			)
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isSideInvisible(BlockState blockState, BlockState blockState2, Direction direction) {
		if (blockState2.getBlock() == this) {
			if (!direction.getAxis().isHorizontal()) {
				return true;
			}

			if ((Boolean)blockState.get((Property)FACING_PROPERTIES.get(direction))
				&& (Boolean)blockState2.get((Property)FACING_PROPERTIES.get(direction.getOpposite()))) {
				return true;
			}
		}

		return super.isSideInvisible(blockState, blockState2, direction);
	}

	public final boolean connectsTo(BlockState blockState, boolean bl) {
		Block block = blockState.getBlock();
		return !canConnect(block) && bl || block instanceof PaneBlock;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}
}
