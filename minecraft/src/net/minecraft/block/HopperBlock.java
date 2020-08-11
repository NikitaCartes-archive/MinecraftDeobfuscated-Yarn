package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class HopperBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = Properties.HOPPER_FACING;
	public static final BooleanProperty ENABLED = Properties.ENABLED;
	private static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0, 10.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape MIDDLE_SHAPE = Block.createCuboidShape(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
	private static final VoxelShape OUTSIDE_SHAPE = VoxelShapes.union(MIDDLE_SHAPE, TOP_SHAPE);
	private static final VoxelShape DEFAULT_SHAPE = VoxelShapes.combineAndSimplify(OUTSIDE_SHAPE, Hopper.INSIDE_SHAPE, BooleanBiFunction.ONLY_FIRST);
	private static final VoxelShape DOWN_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 4.0, 10.0));
	private static final VoxelShape EAST_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(12.0, 4.0, 6.0, 16.0, 8.0, 10.0));
	private static final VoxelShape NORTH_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 4.0, 0.0, 10.0, 8.0, 4.0));
	private static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 4.0, 12.0, 10.0, 8.0, 16.0));
	private static final VoxelShape WEST_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(0.0, 4.0, 6.0, 4.0, 8.0, 10.0));
	private static final VoxelShape DOWN_RAY_TRACE_SHAPE = Hopper.INSIDE_SHAPE;
	private static final VoxelShape EAST_RAY_TRACE_SHAPE = VoxelShapes.union(Hopper.INSIDE_SHAPE, Block.createCuboidShape(12.0, 8.0, 6.0, 16.0, 10.0, 10.0));
	private static final VoxelShape NORTH_RAY_TRACE_SHAPE = VoxelShapes.union(Hopper.INSIDE_SHAPE, Block.createCuboidShape(6.0, 8.0, 0.0, 10.0, 10.0, 4.0));
	private static final VoxelShape SOUTH_RAY_TRACE_SHAPE = VoxelShapes.union(Hopper.INSIDE_SHAPE, Block.createCuboidShape(6.0, 8.0, 12.0, 10.0, 10.0, 16.0));
	private static final VoxelShape WEST_RAY_TRACE_SHAPE = VoxelShapes.union(Hopper.INSIDE_SHAPE, Block.createCuboidShape(0.0, 8.0, 6.0, 4.0, 10.0, 10.0));

	public HopperBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.DOWN).with(ENABLED, Boolean.valueOf(true)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch ((Direction)state.get(FACING)) {
			case DOWN:
				return DOWN_SHAPE;
			case NORTH:
				return NORTH_SHAPE;
			case SOUTH:
				return SOUTH_SHAPE;
			case WEST:
				return WEST_SHAPE;
			case EAST:
				return EAST_SHAPE;
			default:
				return DEFAULT_SHAPE;
		}
	}

	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		switch ((Direction)state.get(FACING)) {
			case DOWN:
				return DOWN_RAY_TRACE_SHAPE;
			case NORTH:
				return NORTH_RAY_TRACE_SHAPE;
			case SOUTH:
				return SOUTH_RAY_TRACE_SHAPE;
			case WEST:
				return WEST_RAY_TRACE_SHAPE;
			case EAST:
				return EAST_RAY_TRACE_SHAPE;
			default:
				return Hopper.INSIDE_SHAPE;
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getSide().getOpposite();
		return this.getDefaultState().with(FACING, direction.getAxis() == Direction.Axis.Y ? Direction.DOWN : direction).with(ENABLED, Boolean.valueOf(true));
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new HopperBlockEntity();
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof HopperBlockEntity) {
				((HopperBlockEntity)blockEntity).setCustomName(itemStack.getName());
			}
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!oldState.isOf(state.getBlock())) {
			this.updateEnabled(world, pos, state);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof HopperBlockEntity) {
				player.openHandledScreen((HopperBlockEntity)blockEntity);
				player.incrementStat(Stats.INSPECT_HOPPER);
			}

			return ActionResult.CONSUME;
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		this.updateEnabled(world, pos, state);
	}

	private void updateEnabled(World world, BlockPos pos, BlockState state) {
		boolean bl = !world.isReceivingRedstonePower(pos);
		if (bl != (Boolean)state.get(ENABLED)) {
			world.setBlockState(pos, state.with(ENABLED, Boolean.valueOf(bl)), 4);
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof HopperBlockEntity) {
				ItemScatterer.spawn(world, pos, (HopperBlockEntity)blockEntity);
				world.updateComparators(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, ENABLED);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof HopperBlockEntity) {
			((HopperBlockEntity)blockEntity).onEntityCollided(entity);
		}
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
