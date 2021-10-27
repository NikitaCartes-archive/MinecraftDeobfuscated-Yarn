package net.minecraft.block;

import java.util.function.ToIntFunction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class LightBlock extends Block implements Waterloggable {
	public static final int field_33722 = 15;
	public static final IntProperty LEVEL_15 = Properties.LEVEL_15;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final ToIntFunction<BlockState> STATE_TO_LUMINANCE = state -> (Integer)state.get(LEVEL_15);

	public LightBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL_15, Integer.valueOf(15)).with(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LEVEL_15, WATERLOGGED);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			world.setBlockState(pos, state.cycle(LEVEL_15), Block.NOTIFY_LISTENERS);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.CONSUME;
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return context.isHolding(Items.LIGHT) ? VoxelShapes.fullCube() : VoxelShapes.empty();
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		ItemStack itemStack = super.getPickStack(world, pos, state);
		if ((Integer)state.get(LEVEL_15) != 15) {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putString(LEVEL_15.getName(), String.valueOf(state.get(LEVEL_15)));
			itemStack.setSubNbt("BlockStateTag", nbtCompound);
		}

		return itemStack;
	}
}
