package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class class_7323 extends Block implements Waterloggable {
	protected static final VoxelShape field_38559 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
	private static final IntProperty field_38561 = Properties.field_38610;
	public static final BooleanProperty field_38560 = Properties.WATERLOGGED;

	public class_7323(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(field_38561, Integer.valueOf(0)).with(field_38560, Boolean.valueOf(false)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(field_38561, field_38560);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return field_38559;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(field_38560)) {
			world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		WorldAccess worldAccess = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		return this.getDefaultState().with(field_38560, Boolean.valueOf(worldAccess.getFluidState(blockPos).getFluid() == Fluids.WATER));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(field_38560) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return !world.isAir(pos.down());
	}

	@Nullable
	public static BlockState method_42879(BlockState blockState) {
		return method_42878(blockState.getBlock().asItem());
	}

	@Nullable
	public static BlockState method_42880(BlockState blockState) {
		return method_42881(blockState) instanceof BlockItem blockItem ? blockItem.getBlock().getDefaultState() : null;
	}

	@Nullable
	public static BlockState method_42878(Item item) {
		if (item == Items.AIR) {
			return null;
		} else {
			int i = Registry.ITEM.getRawId(item);
			return i != -1 ? Blocks.GENERIC_ITEM_BLOCK.getDefaultState().with(field_38561, Integer.valueOf(i)) : null;
		}
	}

	@Nullable
	public static Item method_42881(BlockState blockState) {
		if (blockState.contains(field_38561)) {
			Item item = Registry.ITEM.get((Integer)blockState.get(field_38561));
			return item != Items.AIR ? item : null;
		} else {
			return null;
		}
	}

	public static final class class_7324 extends BlockItem {
		public class_7324(Block block, Item.Settings settings) {
			super(block, settings);
		}

		@Override
		public Text getName(ItemStack stack) {
			return new LiteralText("How did we get here?");
		}
	}
}
