package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
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
	protected static final VoxelShape field_12222 = Block.createCuboidShape(12.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_12214 = Block.createCuboidShape(0.0, 0.0, 0.0, 4.0, 16.0, 16.0);
	protected static final VoxelShape field_12228 = Block.createCuboidShape(0.0, 0.0, 12.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_12213 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 4.0);
	protected static final VoxelShape field_12230 = Block.createCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_12220 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
	protected static final VoxelShape field_12215 = Block.createCuboidShape(6.0, -4.0, 6.0, 10.0, 12.0, 10.0);
	protected static final VoxelShape field_12226 = Block.createCuboidShape(6.0, 4.0, 6.0, 10.0, 20.0, 10.0);
	protected static final VoxelShape field_12221 = Block.createCuboidShape(6.0, 6.0, -4.0, 10.0, 10.0, 12.0);
	protected static final VoxelShape field_12229 = Block.createCuboidShape(6.0, 6.0, 4.0, 10.0, 10.0, 20.0);
	protected static final VoxelShape field_12218 = Block.createCuboidShape(-4.0, 6.0, 6.0, 12.0, 10.0, 10.0);
	protected static final VoxelShape field_12223 = Block.createCuboidShape(4.0, 6.0, 6.0, 20.0, 10.0, 10.0);
	protected static final VoxelShape field_12231 = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 12.0, 10.0);
	protected static final VoxelShape field_12217 = Block.createCuboidShape(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);
	protected static final VoxelShape field_12216 = Block.createCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 12.0);
	protected static final VoxelShape field_12225 = Block.createCuboidShape(6.0, 6.0, 4.0, 10.0, 10.0, 16.0);
	protected static final VoxelShape field_12219 = Block.createCuboidShape(0.0, 6.0, 6.0, 12.0, 10.0, 10.0);
	protected static final VoxelShape field_12212 = Block.createCuboidShape(4.0, 6.0, 6.0, 16.0, 10.0, 10.0);

	public PistonHeadBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.NORTH).with(TYPE, PistonType.field_12637).with(SHORT, Boolean.valueOf(false)));
	}

	private VoxelShape method_11520(BlockState blockState) {
		switch ((Direction)blockState.get(FACING)) {
			case DOWN:
			default:
				return field_12220;
			case UP:
				return field_12230;
			case NORTH:
				return field_12213;
			case SOUTH:
				return field_12228;
			case WEST:
				return field_12214;
			case EAST:
				return field_12222;
		}
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return true;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return VoxelShapes.union(this.method_11520(blockState), this.method_11519(blockState));
	}

	private VoxelShape method_11519(BlockState blockState) {
		boolean bl = (Boolean)blockState.get(SHORT);
		switch ((Direction)blockState.get(FACING)) {
			case DOWN:
			default:
				return bl ? field_12217 : field_12226;
			case UP:
				return bl ? field_12231 : field_12215;
			case NORTH:
				return bl ? field_12225 : field_12229;
			case SOUTH:
				return bl ? field_12216 : field_12221;
			case WEST:
				return bl ? field_12212 : field_12223;
			case EAST:
				return bl ? field_12219 : field_12218;
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
			? Blocks.AIR.getDefaultState()
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
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		return blockState.with(FACING, rotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, Mirror mirror) {
		return blockState.rotate(mirror.getRotation(blockState.get(FACING)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(FACING, TYPE, SHORT);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
