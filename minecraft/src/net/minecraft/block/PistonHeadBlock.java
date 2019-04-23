package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

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

	public PistonHeadBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory.getDefaultState().with(FACING, Direction.field_11043).with(TYPE, PistonType.field_12637).with(SHORT, Boolean.valueOf(false))
		);
	}

	private VoxelShape getHeadShape(BlockState blockState) {
		switch ((Direction)blockState.get(FACING)) {
			case field_11033:
			default:
				return DOWN_HEAD_SHAPE;
			case field_11036:
				return UP_HEAD_SHAPE;
			case field_11043:
				return NORTH_HEAD_SHAPE;
			case field_11035:
				return SOUTH_HEAD_SHAPE;
			case field_11039:
				return WEST_HEAD_SHAPE;
			case field_11034:
				return EAST_HEAD_SHAPE;
		}
	}

	@Override
	public boolean hasSidedTransparency(BlockState blockState) {
		return true;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return VoxelShapes.union(this.getHeadShape(blockState), this.getArmShape(blockState));
	}

	private VoxelShape getArmShape(BlockState blockState) {
		boolean bl = (Boolean)blockState.get(SHORT);
		switch ((Direction)blockState.get(FACING)) {
			case field_11033:
			default:
				return bl ? SHORT_DOWN_ARM_SHAPE : DOWN_ARM_SHAPE;
			case field_11036:
				return bl ? SHORT_UP_ARM_SHAPE : UP_ARM_SHAPE;
			case field_11043:
				return bl ? SHORT_NORTH_ARM_SHAPE : NORTH_ARM_SHAPE;
			case field_11035:
				return bl ? SHORT_SOUTH_ARM_SHAPE : SOUTH_ARM_SHAPE;
			case field_11039:
				return bl ? SHORT_WEST_ARM_SHAPE : WEST_ARM_SHAPE;
			case field_11034:
				return bl ? SHORT_EAST_ARM_SHAPE : EAST_ARM_SHAPE;
		}
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		if (!world.isClient && playerEntity.abilities.creativeMode) {
			BlockPos blockPos2 = blockPos.offset(((Direction)blockState.get(FACING)).getOpposite());
			Block block = world.getBlockState(blockPos2).getBlock();
			if (block == Blocks.field_10560 || block == Blocks.field_10615) {
				world.clearBlockState(blockPos2, false);
			}
		}

		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
			Direction direction = ((Direction)blockState.get(FACING)).getOpposite();
			blockPos = blockPos.offset(direction);
			BlockState blockState3 = world.getBlockState(blockPos);
			if ((blockState3.getBlock() == Blocks.field_10560 || blockState3.getBlock() == Blocks.field_10615) && (Boolean)blockState3.get(PistonBlock.EXTENDED)) {
				dropStacks(blockState3, world, blockPos);
				world.clearBlockState(blockPos, false);
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return direction.getOpposite() == blockState.get(FACING) && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Block block = viewableWorld.getBlockState(blockPos.offset(((Direction)blockState.get(FACING)).getOpposite())).getBlock();
		return block == Blocks.field_10560 || block == Blocks.field_10615 || block == Blocks.field_10008;
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (blockState.canPlaceAt(world, blockPos)) {
			BlockPos blockPos3 = blockPos.offset(((Direction)blockState.get(FACING)).getOpposite());
			world.getBlockState(blockPos3).neighborUpdate(world, blockPos3, block, blockPos2, false);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(blockState.get(TYPE) == PistonType.field_12634 ? Blocks.field_10615 : Blocks.field_10560);
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		return blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(FACING, TYPE, SHORT);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
