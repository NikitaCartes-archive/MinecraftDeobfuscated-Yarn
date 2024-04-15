package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.ToIntFunction;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class LightBlock extends Block implements Waterloggable {
	public static final MapCodec<LightBlock> CODEC = createCodec(LightBlock::new);
	public static final int field_33722 = 15;
	public static final IntProperty LEVEL_15 = Properties.LEVEL_15;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final ToIntFunction<BlockState> STATE_TO_LUMINANCE = state -> (Integer)state.get(LEVEL_15);

	@Override
	public MapCodec<LightBlock> getCodec() {
		return CODEC;
	}

	public LightBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL_15, Integer.valueOf(15)).with(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LEVEL_15, WATERLOGGED);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (!world.isClient && player.isCreativeLevelTwoOp()) {
			world.setBlockState(pos, state.cycle(LEVEL_15), Block.NOTIFY_LISTENERS);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.CONSUME;
		}
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return context.isHolding(Items.LIGHT) ? VoxelShapes.fullCube() : VoxelShapes.empty();
	}

	@Override
	protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
		return state.getFluidState().isEmpty();
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return 1.0F;
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
		return addNbtForLevel(super.getPickStack(world, pos, state), (Integer)state.get(LEVEL_15));
	}

	public static ItemStack addNbtForLevel(ItemStack stack, int level) {
		if (level != 15) {
			stack.set(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT.with(LEVEL_15, level));
		}

		return stack;
	}
}
