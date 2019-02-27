package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class SlabBlock extends Block implements Waterloggable {
	public static final EnumProperty<SlabType> TYPE = Properties.SLAB_TYPE;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	protected static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);

	public SlabBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(TYPE, SlabType.field_12681).with(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return blockState.get(TYPE) != SlabType.field_12682;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(TYPE, WATERLOGGED);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		SlabType slabType = blockState.get(TYPE);
		switch (slabType) {
			case field_12682:
				return VoxelShapes.fullCube();
			case field_12679:
				return TOP_SHAPE;
			default:
				return BOTTOM_SHAPE;
		}
	}

	@Override
	public boolean hasSolidTopSurface(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.get(TYPE) == SlabType.field_12682 || blockState.get(TYPE) == SlabType.field_12679;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		BlockState blockState = itemPlacementContext.getWorld().getBlockState(blockPos);
		if (blockState.getBlock() == this) {
			return blockState.with(TYPE, SlabType.field_12682).with(WATERLOGGED, Boolean.valueOf(false));
		} else {
			FluidState fluidState = itemPlacementContext.getWorld().getFluidState(blockPos);
			BlockState blockState2 = this.getDefaultState().with(TYPE, SlabType.field_12681).with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
			Direction direction = itemPlacementContext.getFacing();
			return direction != Direction.DOWN && (direction == Direction.UP || !(itemPlacementContext.getPos().y - (double)blockPos.getY() > 0.5))
				? blockState2
				: blockState2.with(TYPE, SlabType.field_12679);
		}
	}

	@Override
	public boolean canReplace(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		ItemStack itemStack = itemPlacementContext.getItemStack();
		SlabType slabType = blockState.get(TYPE);
		if (slabType == SlabType.field_12682 || itemStack.getItem() != this.getItem()) {
			return false;
		} else if (itemPlacementContext.method_7717()) {
			boolean bl = itemPlacementContext.getPos().y - (double)itemPlacementContext.getBlockPos().getY() > 0.5;
			Direction direction = itemPlacementContext.getFacing();
			return slabType == SlabType.field_12681
				? direction == Direction.UP || bl && direction.getAxis().isHorizontal()
				: direction == Direction.DOWN || !bl && direction.getAxis().isHorizontal();
		} else {
			return true;
		}
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.get(WATERLOGGED) ? Fluids.WATER.getState(false) : super.getFluidState(blockState);
	}

	@Override
	public boolean tryFillWithFluid(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		return blockState.get(TYPE) != SlabType.field_12682 ? Waterloggable.super.tryFillWithFluid(iWorld, blockPos, blockState, fluidState) : false;
	}

	@Override
	public boolean canFillWithFluid(BlockView blockView, BlockPos blockPos, BlockState blockState, Fluid fluid) {
		return blockState.get(TYPE) != SlabType.field_12682 ? Waterloggable.super.canFillWithFluid(blockView, blockPos, blockState, fluid) : false;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if ((Boolean)blockState.get(WATERLOGGED)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		switch (blockPlacementEnvironment) {
			case field_50:
				return blockState.get(TYPE) == SlabType.field_12681;
			case field_48:
				return blockView.getFluidState(blockPos).matches(FluidTags.field_15517);
			case field_51:
				return false;
			default:
				return false;
		}
	}
}
