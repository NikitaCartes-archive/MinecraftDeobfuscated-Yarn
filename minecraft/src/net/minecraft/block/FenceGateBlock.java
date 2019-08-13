package net.minecraft.block;

import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FenceGateBlock extends HorizontalFacingBlock {
	public static final BooleanProperty OPEN = Properties.OPEN;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final BooleanProperty IN_WALL = Properties.IN_WALL;
	protected static final VoxelShape Z_AXIS_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
	protected static final VoxelShape X_AXIS_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
	protected static final VoxelShape IN_WALL_Z_AXIS_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 13.0, 10.0);
	protected static final VoxelShape IN_WALL_X_AXIS_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 13.0, 16.0);
	protected static final VoxelShape Z_AXIS_COLLISION_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 24.0, 10.0);
	protected static final VoxelShape X_AXIS_COLLISION_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 24.0, 16.0);
	protected static final VoxelShape field_11018 = VoxelShapes.union(
		Block.createCuboidShape(0.0, 5.0, 7.0, 2.0, 16.0, 9.0), Block.createCuboidShape(14.0, 5.0, 7.0, 16.0, 16.0, 9.0)
	);
	protected static final VoxelShape field_11023 = VoxelShapes.union(
		Block.createCuboidShape(7.0, 5.0, 0.0, 9.0, 16.0, 2.0), Block.createCuboidShape(7.0, 5.0, 14.0, 9.0, 16.0, 16.0)
	);
	protected static final VoxelShape field_11020 = VoxelShapes.union(
		Block.createCuboidShape(0.0, 2.0, 7.0, 2.0, 13.0, 9.0), Block.createCuboidShape(14.0, 2.0, 7.0, 16.0, 13.0, 9.0)
	);
	protected static final VoxelShape field_11027 = VoxelShapes.union(
		Block.createCuboidShape(7.0, 2.0, 0.0, 9.0, 13.0, 2.0), Block.createCuboidShape(7.0, 2.0, 14.0, 9.0, 13.0, 16.0)
	);

	public FenceGateBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory.getDefaultState().with(OPEN, Boolean.valueOf(false)).with(POWERED, Boolean.valueOf(false)).with(IN_WALL, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		if ((Boolean)blockState.get(IN_WALL)) {
			return ((Direction)blockState.get(FACING)).getAxis() == Direction.Axis.field_11048 ? IN_WALL_X_AXIS_SHAPE : IN_WALL_Z_AXIS_SHAPE;
		} else {
			return ((Direction)blockState.get(FACING)).getAxis() == Direction.Axis.field_11048 ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		Direction.Axis axis = direction.getAxis();
		if (((Direction)blockState.get(FACING)).rotateYClockwise().getAxis() != axis) {
			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			boolean bl = this.isWall(blockState2) || this.isWall(iWorld.getBlockState(blockPos.offset(direction.getOpposite())));
			return blockState.with(IN_WALL, Boolean.valueOf(bl));
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		if ((Boolean)blockState.get(OPEN)) {
			return VoxelShapes.empty();
		} else {
			return ((Direction)blockState.get(FACING)).getAxis() == Direction.Axis.field_11051 ? Z_AXIS_COLLISION_SHAPE : X_AXIS_COLLISION_SHAPE;
		}
	}

	@Override
	public VoxelShape method_9571(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		if ((Boolean)blockState.get(IN_WALL)) {
			return ((Direction)blockState.get(FACING)).getAxis() == Direction.Axis.field_11048 ? field_11027 : field_11020;
		} else {
			return ((Direction)blockState.get(FACING)).getAxis() == Direction.Axis.field_11048 ? field_11023 : field_11018;
		}
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		switch (blockPlacementEnvironment) {
			case field_50:
				return (Boolean)blockState.get(OPEN);
			case field_48:
				return false;
			case field_51:
				return (Boolean)blockState.get(OPEN);
			default:
				return false;
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		World world = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		boolean bl = world.isReceivingRedstonePower(blockPos);
		Direction direction = itemPlacementContext.getPlayerFacing();
		Direction.Axis axis = direction.getAxis();
		boolean bl2 = axis == Direction.Axis.field_11051 && (this.isWall(world.getBlockState(blockPos.west())) || this.isWall(world.getBlockState(blockPos.east())))
			|| axis == Direction.Axis.field_11048 && (this.isWall(world.getBlockState(blockPos.north())) || this.isWall(world.getBlockState(blockPos.south())));
		return this.getDefaultState().with(FACING, direction).with(OPEN, Boolean.valueOf(bl)).with(POWERED, Boolean.valueOf(bl)).with(IN_WALL, Boolean.valueOf(bl2));
	}

	private boolean isWall(BlockState blockState) {
		return blockState.getBlock().matches(BlockTags.field_15504);
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if ((Boolean)blockState.get(OPEN)) {
			blockState = blockState.with(OPEN, Boolean.valueOf(false));
			world.setBlockState(blockPos, blockState, 10);
		} else {
			Direction direction = playerEntity.getHorizontalFacing();
			if (blockState.get(FACING) == direction.getOpposite()) {
				blockState = blockState.with(FACING, direction);
			}

			blockState = blockState.with(OPEN, Boolean.valueOf(true));
			world.setBlockState(blockPos, blockState, 10);
		}

		world.playLevelEvent(playerEntity, blockState.get(OPEN) ? 1008 : 1014, blockPos, 0);
		return true;
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (!world.isClient) {
			boolean bl2 = world.isReceivingRedstonePower(blockPos);
			if ((Boolean)blockState.get(POWERED) != bl2) {
				world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(bl2)).with(OPEN, Boolean.valueOf(bl2)), 2);
				if ((Boolean)blockState.get(OPEN) != bl2) {
					world.playLevelEvent(null, bl2 ? 1008 : 1014, blockPos, 0);
				}
			}
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(FACING, OPEN, POWERED, IN_WALL);
	}

	public static boolean canWallConnect(BlockState blockState, Direction direction) {
		return ((Direction)blockState.get(FACING)).getAxis() == direction.rotateYClockwise().getAxis();
	}
}
