package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.EntityContext;
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
	public static final EnumProperty<SlabType> field_11501 = Properties.field_12485;
	public static final BooleanProperty field_11502 = Properties.field_12508;
	protected static final VoxelShape field_11500 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	protected static final VoxelShape field_11499 = Block.method_9541(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);

	public SlabBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.method_9564().method_11657(field_11501, SlabType.field_12681).method_11657(field_11502, Boolean.valueOf(false)));
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return blockState.method_11654(field_11501) != SlabType.field_12682;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11501, field_11502);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		SlabType slabType = blockState.method_11654(field_11501);
		switch (slabType) {
			case field_12682:
				return VoxelShapes.method_1077();
			case field_12679:
				return field_11499;
			default:
				return field_11500;
		}
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		BlockState blockState = itemPlacementContext.method_8045().method_8320(blockPos);
		if (blockState.getBlock() == this) {
			return blockState.method_11657(field_11501, SlabType.field_12682).method_11657(field_11502, Boolean.valueOf(false));
		} else {
			FluidState fluidState = itemPlacementContext.method_8045().method_8316(blockPos);
			BlockState blockState2 = this.method_9564()
				.method_11657(field_11501, SlabType.field_12681)
				.method_11657(field_11502, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
			Direction direction = itemPlacementContext.getSide();
			return direction != Direction.field_11033
					&& (direction == Direction.field_11036 || !(itemPlacementContext.method_17698().y - (double)blockPos.getY() > 0.5))
				? blockState2
				: blockState2.method_11657(field_11501, SlabType.field_12679);
		}
	}

	@Override
	public boolean method_9616(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		ItemStack itemStack = itemPlacementContext.getStack();
		SlabType slabType = blockState.method_11654(field_11501);
		if (slabType == SlabType.field_12682 || itemStack.getItem() != this.asItem()) {
			return false;
		} else if (itemPlacementContext.canReplaceExisting()) {
			boolean bl = itemPlacementContext.method_17698().y - (double)itemPlacementContext.getBlockPos().getY() > 0.5;
			Direction direction = itemPlacementContext.getSide();
			return slabType == SlabType.field_12681
				? direction == Direction.field_11036 || bl && direction.getAxis().isHorizontal()
				: direction == Direction.field_11033 || !bl && direction.getAxis().isHorizontal();
		} else {
			return true;
		}
	}

	@Override
	public FluidState method_9545(BlockState blockState) {
		return blockState.method_11654(field_11502) ? Fluids.WATER.method_15729(false) : super.method_9545(blockState);
	}

	@Override
	public boolean method_10311(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		return blockState.method_11654(field_11501) != SlabType.field_12682 ? Waterloggable.super.method_10311(iWorld, blockPos, blockState, fluidState) : false;
	}

	@Override
	public boolean method_10310(BlockView blockView, BlockPos blockPos, BlockState blockState, Fluid fluid) {
		return blockState.method_11654(field_11501) != SlabType.field_12682 ? Waterloggable.super.method_10310(blockView, blockPos, blockState, fluid) : false;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.method_11654(field_11502)) {
			iWorld.method_8405().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		switch (blockPlacementEnvironment) {
			case field_50:
				return false;
			case field_48:
				return blockView.method_8316(blockPos).matches(FluidTags.field_15517);
			case field_51:
				return false;
			default:
				return false;
		}
	}
}
