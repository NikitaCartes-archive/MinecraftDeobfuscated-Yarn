package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.enums.SlabType;
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
	public static final EnumProperty<SlabType> field_11501 = Properties.SLAB_TYPE;
	public static final BooleanProperty field_11502 = Properties.WATERLOGGED;
	protected static final VoxelShape field_11500 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	protected static final VoxelShape field_11499 = Block.createCubeShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);

	public SlabBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(field_11501, SlabType.field_12681).with(field_11502, Boolean.valueOf(false)));
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return blockState.get(field_11501) != SlabType.field_12682;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11501, field_11502);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		SlabType slabType = blockState.get(field_11501);
		switch (slabType) {
			case field_12682:
				return VoxelShapes.fullCube();
			case field_12679:
				return field_11499;
			default:
				return field_11500;
		}
	}

	@Override
	public boolean hasSolidTopSurface(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.get(field_11501) == SlabType.field_12682 || blockState.get(field_11501) == SlabType.field_12679;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getPos());
		if (blockState.getBlock() == this) {
			return blockState.with(field_11501, SlabType.field_12682).with(field_11502, Boolean.valueOf(false));
		} else {
			FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getPos());
			BlockState blockState2 = this.getDefaultState()
				.with(field_11501, SlabType.field_12681)
				.with(field_11502, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
			Direction direction = itemPlacementContext.getFacing();
			return direction != Direction.DOWN && (direction == Direction.UP || !((double)itemPlacementContext.getHitY() > 0.5))
				? blockState2
				: blockState2.with(field_11501, SlabType.field_12679);
		}
	}

	@Override
	public boolean method_9616(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		ItemStack itemStack = itemPlacementContext.getItemStack();
		SlabType slabType = blockState.get(field_11501);
		if (slabType == SlabType.field_12682 || itemStack.getItem() != this.getItem()) {
			return false;
		} else if (itemPlacementContext.method_7717()) {
			boolean bl = (double)itemPlacementContext.getHitY() > 0.5;
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
		return blockState.get(field_11502) ? Fluids.WATER.getState(false) : super.getFluidState(blockState);
	}

	@Override
	public boolean tryFillWithFluid(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		return blockState.get(field_11501) != SlabType.field_12682 ? Waterloggable.super.tryFillWithFluid(iWorld, blockPos, blockState, fluidState) : false;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if ((Boolean)blockState.get(field_11502)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.method_15789(iWorld));
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		switch (placementEnvironment) {
			case field_50:
				return blockState.get(field_11501) == SlabType.field_12681;
			case field_48:
				return blockView.getFluidState(blockPos).matches(FluidTags.field_15517);
			case field_51:
				return false;
			default:
				return false;
		}
	}
}
