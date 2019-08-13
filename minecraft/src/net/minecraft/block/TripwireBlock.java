package net.minecraft.block;

import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class TripwireBlock extends Block {
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final BooleanProperty ATTACHED = Properties.ATTACHED;
	public static final BooleanProperty DISARMED = Properties.DISARMED;
	public static final BooleanProperty NORTH = ConnectedPlantBlock.NORTH;
	public static final BooleanProperty EAST = ConnectedPlantBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectedPlantBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectedPlantBlock.WEST;
	private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = HorizontalConnectedBlock.FACING_PROPERTIES;
	protected static final VoxelShape ATTACHED_SHAPE = Block.createCuboidShape(0.0, 1.0, 0.0, 16.0, 2.5, 16.0);
	protected static final VoxelShape DETACHED_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	private final TripwireHookBlock hookBlock;

	public TripwireBlock(TripwireHookBlock tripwireHookBlock, Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(POWERED, Boolean.valueOf(false))
				.with(ATTACHED, Boolean.valueOf(false))
				.with(DISARMED, Boolean.valueOf(false))
				.with(NORTH, Boolean.valueOf(false))
				.with(EAST, Boolean.valueOf(false))
				.with(SOUTH, Boolean.valueOf(false))
				.with(WEST, Boolean.valueOf(false))
		);
		this.hookBlock = tripwireHookBlock;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return blockState.get(ATTACHED) ? ATTACHED_SHAPE : DETACHED_SHAPE;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		return this.getDefaultState()
			.with(NORTH, Boolean.valueOf(this.shouldConnectTo(blockView.getBlockState(blockPos.north()), Direction.field_11043)))
			.with(EAST, Boolean.valueOf(this.shouldConnectTo(blockView.getBlockState(blockPos.east()), Direction.field_11034)))
			.with(SOUTH, Boolean.valueOf(this.shouldConnectTo(blockView.getBlockState(blockPos.south()), Direction.field_11035)))
			.with(WEST, Boolean.valueOf(this.shouldConnectTo(blockView.getBlockState(blockPos.west()), Direction.field_11039)));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return direction.getAxis().isHorizontal()
			? blockState.with((Property)FACING_PROPERTIES.get(direction), Boolean.valueOf(this.shouldConnectTo(blockState2, direction)))
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9179;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			this.update(world, blockPos, blockState);
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			this.update(world, blockPos, blockState.with(POWERED, Boolean.valueOf(true)));
		}
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		if (!world.isClient && !playerEntity.getMainHandStack().isEmpty() && playerEntity.getMainHandStack().getItem() == Items.field_8868) {
			world.setBlockState(blockPos, blockState.with(DISARMED, Boolean.valueOf(true)), 4);
		}

		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	private void update(World world, BlockPos blockPos, BlockState blockState) {
		for (Direction direction : new Direction[]{Direction.field_11035, Direction.field_11039}) {
			for (int i = 1; i < 42; i++) {
				BlockPos blockPos2 = blockPos.offset(direction, i);
				BlockState blockState2 = world.getBlockState(blockPos2);
				if (blockState2.getBlock() == this.hookBlock) {
					if (blockState2.get(TripwireHookBlock.FACING) == direction.getOpposite()) {
						this.hookBlock.update(world, blockPos2, blockState2, false, true, i, blockState);
					}
					break;
				}

				if (blockState2.getBlock() != this) {
					break;
				}
			}
		}
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!world.isClient) {
			if (!(Boolean)blockState.get(POWERED)) {
				this.updatePowered(world, blockPos);
			}
		}
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			if ((Boolean)world.getBlockState(blockPos).get(POWERED)) {
				this.updatePowered(world, blockPos);
			}
		}
	}

	private void updatePowered(World world, BlockPos blockPos) {
		BlockState blockState = world.getBlockState(blockPos);
		boolean bl = (Boolean)blockState.get(POWERED);
		boolean bl2 = false;
		List<? extends Entity> list = world.getEntities(null, blockState.getOutlineShape(world, blockPos).getBoundingBox().offset(blockPos));
		if (!list.isEmpty()) {
			for (Entity entity : list) {
				if (!entity.canAvoidTraps()) {
					bl2 = true;
					break;
				}
			}
		}

		if (bl2 != bl) {
			blockState = blockState.with(POWERED, Boolean.valueOf(bl2));
			world.setBlockState(blockPos, blockState, 3);
			this.update(world, blockPos, blockState);
		}

		if (bl2) {
			world.getBlockTickScheduler().schedule(new BlockPos(blockPos), this, this.getTickRate(world));
		}
	}

	public boolean shouldConnectTo(BlockState blockState, Direction direction) {
		Block block = blockState.getBlock();
		return block == this.hookBlock ? blockState.get(TripwireHookBlock.FACING) == direction.getOpposite() : block == this;
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		switch (blockRotation) {
			case field_11464:
				return blockState.with(NORTH, blockState.get(SOUTH)).with(EAST, blockState.get(WEST)).with(SOUTH, blockState.get(NORTH)).with(WEST, blockState.get(EAST));
			case field_11465:
				return blockState.with(NORTH, blockState.get(EAST)).with(EAST, blockState.get(SOUTH)).with(SOUTH, blockState.get(WEST)).with(WEST, blockState.get(NORTH));
			case field_11463:
				return blockState.with(NORTH, blockState.get(WEST)).with(EAST, blockState.get(NORTH)).with(SOUTH, blockState.get(EAST)).with(WEST, blockState.get(SOUTH));
			default:
				return blockState;
		}
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		switch (blockMirror) {
			case field_11300:
				return blockState.with(NORTH, blockState.get(SOUTH)).with(SOUTH, blockState.get(NORTH));
			case field_11301:
				return blockState.with(EAST, blockState.get(WEST)).with(WEST, blockState.get(EAST));
			default:
				return super.mirror(blockState, blockMirror);
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
	}
}
