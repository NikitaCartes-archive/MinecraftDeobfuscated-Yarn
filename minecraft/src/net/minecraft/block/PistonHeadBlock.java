package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class PistonHeadBlock extends FacingBlock {
	public static final EnumProperty<PistonType> TYPE = Properties.PISTON_TYPE;
	public static final BooleanProperty SHORT = Properties.SHORT;
	protected static final VoxelShape EAST_HEAD_SHAPE = Block.createCuboidShape(12.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape WEST_HEAD_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 4.0, 16.0, 16.0);
	protected static final VoxelShape SOUTH_HEAD_SHAPE = Block.createCuboidShape(0.0, 0.0, 12.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape NORTH_HEAD_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 4.0);
	protected static final VoxelShape UP_HEAD_SHAPE = Block.createCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape DOWN_HEAD_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
	protected static final VoxelShape UP_ARM_SHAPE = Block.createCuboidShape(6.0, -4.0, 6.0, 10.0, 12.0, 10.0);
	protected static final VoxelShape DOWN_ARM_SHAPE = Block.createCuboidShape(6.0, 4.0, 6.0, 10.0, 20.0, 10.0);
	protected static final VoxelShape SOUTH_ARM_SHAPE = Block.createCuboidShape(6.0, 6.0, -4.0, 10.0, 10.0, 12.0);
	protected static final VoxelShape NORTH_ARM_SHAPE = Block.createCuboidShape(6.0, 6.0, 4.0, 10.0, 10.0, 20.0);
	protected static final VoxelShape EAST_ARM_SHAPE = Block.createCuboidShape(-4.0, 6.0, 6.0, 12.0, 10.0, 10.0);
	protected static final VoxelShape WEST_ARM_SHAPE = Block.createCuboidShape(4.0, 6.0, 6.0, 20.0, 10.0, 10.0);
	protected static final VoxelShape SHORT_UP_ARM_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 12.0, 10.0);
	protected static final VoxelShape SHORT_DOWN_ARM_SHAPE = Block.createCuboidShape(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);
	protected static final VoxelShape SHORT_SOUTH_ARM_SHAPE = Block.createCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 12.0);
	protected static final VoxelShape SHORT_NORTH_ARM_SHAPE = Block.createCuboidShape(6.0, 6.0, 4.0, 10.0, 10.0, 16.0);
	protected static final VoxelShape SHORT_EAST_ARM_SHAPE = Block.createCuboidShape(0.0, 6.0, 6.0, 12.0, 10.0, 10.0);
	protected static final VoxelShape SHORT_WEST_ARM_SHAPE = Block.createCuboidShape(4.0, 6.0, 6.0, 16.0, 10.0, 10.0);

	public PistonHeadBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TYPE, PistonType.DEFAULT).with(SHORT, Boolean.valueOf(false)));
	}

	private VoxelShape getHeadShape(BlockState state) {
		switch ((Direction)state.get(FACING)) {
			case DOWN:
			default:
				return DOWN_HEAD_SHAPE;
			case UP:
				return UP_HEAD_SHAPE;
			case NORTH:
				return NORTH_HEAD_SHAPE;
			case SOUTH:
				return SOUTH_HEAD_SHAPE;
			case WEST:
				return WEST_HEAD_SHAPE;
			case EAST:
				return EAST_HEAD_SHAPE;
		}
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.union(this.getHeadShape(state), this.getArmShape(state));
	}

	private VoxelShape getArmShape(BlockState state) {
		boolean bl = (Boolean)state.get(SHORT);
		switch ((Direction)state.get(FACING)) {
			case DOWN:
			default:
				return bl ? SHORT_DOWN_ARM_SHAPE : DOWN_ARM_SHAPE;
			case UP:
				return bl ? SHORT_UP_ARM_SHAPE : UP_ARM_SHAPE;
			case NORTH:
				return bl ? SHORT_NORTH_ARM_SHAPE : NORTH_ARM_SHAPE;
			case SOUTH:
				return bl ? SHORT_SOUTH_ARM_SHAPE : SOUTH_ARM_SHAPE;
			case WEST:
				return bl ? SHORT_WEST_ARM_SHAPE : WEST_ARM_SHAPE;
			case EAST:
				return bl ? SHORT_EAST_ARM_SHAPE : EAST_ARM_SHAPE;
		}
	}

	private boolean method_26980(BlockState blockState, BlockState blockState2) {
		Block block = blockState.get(TYPE) == PistonType.DEFAULT ? Blocks.PISTON : Blocks.STICKY_PISTON;
		return blockState2.isOf(block) && (Boolean)blockState2.get(PistonBlock.EXTENDED) && blockState2.get(FACING) == blockState.get(FACING);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClient && player.abilities.creativeMode) {
			BlockPos blockPos = pos.offset(((Direction)state.get(FACING)).getOpposite());
			if (this.method_26980(state, world.getBlockState(blockPos))) {
				world.breakBlock(blockPos, false);
			}
		}

		super.onBreak(world, pos, state, player);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
		if (!state.isOf(newState.getBlock())) {
			super.onStateReplaced(state, world, pos, newState, notify);
			BlockPos blockPos = pos.offset(((Direction)state.get(FACING)).getOpposite());
			if (this.method_26980(state, world.getBlockState(blockPos))) {
				world.breakBlock(blockPos, true);
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		return direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.offset(((Direction)state.get(FACING)).getOpposite()));
		return this.method_26980(state, blockState) || blockState.isOf(Blocks.MOVING_PISTON) && blockState.get(FACING) == state.get(FACING);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (state.canPlaceAt(world, pos)) {
			BlockPos blockPos = pos.offset(((Direction)state.get(FACING)).getOpposite());
			world.getBlockState(blockPos).neighborUpdate(world, blockPos, block, fromPos, false);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(state.get(TYPE) == PistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
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
		builder.add(FACING, TYPE, SHORT);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
