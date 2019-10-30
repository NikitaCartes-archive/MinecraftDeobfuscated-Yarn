package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
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
		this.setDefaultState(this.getDefaultState().with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return state.get(TYPE) != SlabType.DOUBLE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(TYPE, WATERLOGGED);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		SlabType slabType = state.get(TYPE);
		switch (slabType) {
			case DOUBLE:
				return VoxelShapes.fullCube();
			case TOP:
				return TOP_SHAPE;
			default:
				return BOTTOM_SHAPE;
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos blockPos = ctx.getBlockPos();
		BlockState blockState = ctx.getWorld().getBlockState(blockPos);
		if (blockState.getBlock() == this) {
			return blockState.with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, Boolean.valueOf(false));
		} else {
			FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
			BlockState blockState2 = this.getDefaultState().with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
			Direction direction = ctx.getSide();
			return direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getHitPos().y - (double)blockPos.getY() > 0.5))
				? blockState2
				: blockState2.with(TYPE, SlabType.TOP);
		}
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
		ItemStack itemStack = ctx.getStack();
		SlabType slabType = state.get(TYPE);
		if (slabType == SlabType.DOUBLE || itemStack.getItem() != this.asItem()) {
			return false;
		} else if (ctx.canReplaceExisting()) {
			boolean bl = ctx.getHitPos().y - (double)ctx.getBlockPos().getY() > 0.5;
			Direction direction = ctx.getSide();
			return slabType == SlabType.BOTTOM
				? direction == Direction.UP || bl && direction.getAxis().isHorizontal()
				: direction == Direction.DOWN || !bl && direction.getAxis().isHorizontal();
		} else {
			return true;
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean tryFillWithFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
		return state.get(TYPE) != SlabType.DOUBLE ? Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState) : false;
	}

	@Override
	public boolean canFillWithFluid(BlockView view, BlockPos pos, BlockState state, Fluid fluid) {
		return state.get(TYPE) != SlabType.DOUBLE ? Waterloggable.super.canFillWithFluid(view, pos, state, fluid) : false;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
		switch (env) {
			case LAND:
				return false;
			case WATER:
				return view.getFluidState(pos).matches(FluidTags.WATER);
			case AIR:
				return false;
			default:
				return false;
		}
	}
}
