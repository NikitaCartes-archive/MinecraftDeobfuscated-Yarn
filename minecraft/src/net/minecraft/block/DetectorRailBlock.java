package net.minecraft.block;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.enums.RailShape;
import net.minecraft.container.Container;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class DetectorRailBlock extends AbstractRailBlock {
	public static final EnumProperty<RailShape> SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
	public static final BooleanProperty POWERED = Properties.POWERED;

	public DetectorRailBlock(Block.Settings settings) {
		super(true, settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(POWERED, Boolean.valueOf(false)).with(SHAPE, RailShape.NORTH_SOUTH));
	}

	@Override
	public int getTickRate(WorldView worldView) {
		return 20;
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!world.isClient) {
			if (!(Boolean)blockState.get(POWERED)) {
				this.updatePoweredStatus(world, blockPos, blockState);
			}
		}
	}

	@Override
	public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.get(POWERED)) {
			this.updatePoweredStatus(serverWorld, blockPos, blockState);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.get(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		if (!(Boolean)blockState.get(POWERED)) {
			return 0;
		} else {
			return direction == Direction.UP ? 15 : 0;
		}
	}

	private void updatePoweredStatus(World world, BlockPos blockPos, BlockState blockState) {
		boolean bl = (Boolean)blockState.get(POWERED);
		boolean bl2 = false;
		List<AbstractMinecartEntity> list = this.getCarts(world, blockPos, AbstractMinecartEntity.class, null);
		if (!list.isEmpty()) {
			bl2 = true;
		}

		if (bl2 && !bl) {
			BlockState blockState2 = blockState.with(POWERED, Boolean.valueOf(true));
			world.setBlockState(blockPos, blockState2, 3);
			this.updateNearbyRails(world, blockPos, blockState2, true);
			world.updateNeighborsAlways(blockPos, this);
			world.updateNeighborsAlways(blockPos.method_10074(), this);
			world.scheduleBlockRender(blockPos, blockState, blockState2);
		}

		if (!bl2 && bl) {
			BlockState blockState2 = blockState.with(POWERED, Boolean.valueOf(false));
			world.setBlockState(blockPos, blockState2, 3);
			this.updateNearbyRails(world, blockPos, blockState2, false);
			world.updateNeighborsAlways(blockPos, this);
			world.updateNeighborsAlways(blockPos.method_10074(), this);
			world.scheduleBlockRender(blockPos, blockState, blockState2);
		}

		if (bl2) {
			world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
		}

		world.updateHorizontalAdjacent(blockPos, this);
	}

	protected void updateNearbyRails(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
		RailPlacementHelper railPlacementHelper = new RailPlacementHelper(world, blockPos, blockState);

		for (BlockPos blockPos2 : railPlacementHelper.getNeighbors()) {
			BlockState blockState2 = world.getBlockState(blockPos2);
			blockState2.neighborUpdate(world, blockPos2, blockState2.getBlock(), blockPos, false);
		}
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			super.onBlockAdded(blockState, world, blockPos, blockState2, bl);
			this.updatePoweredStatus(world, blockPos, blockState);
		}
	}

	@Override
	public Property<RailShape> getShapeProperty() {
		return SHAPE;
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		if ((Boolean)blockState.get(POWERED)) {
			List<CommandBlockMinecartEntity> list = this.getCarts(world, blockPos, CommandBlockMinecartEntity.class, null);
			if (!list.isEmpty()) {
				return ((CommandBlockMinecartEntity)list.get(0)).getCommandExecutor().getSuccessCount();
			}

			List<AbstractMinecartEntity> list2 = this.getCarts(world, blockPos, AbstractMinecartEntity.class, EntityPredicates.VALID_INVENTORIES);
			if (!list2.isEmpty()) {
				return Container.calculateComparatorOutput((Inventory)list2.get(0));
			}
		}

		return 0;
	}

	protected <T extends AbstractMinecartEntity> List<T> getCarts(World world, BlockPos blockPos, Class<T> class_, @Nullable Predicate<Entity> predicate) {
		return world.getEntities(class_, this.getCartDetectionBox(blockPos), predicate);
	}

	private Box getCartDetectionBox(BlockPos blockPos) {
		float f = 0.2F;
		return new Box(
			(double)((float)blockPos.getX() + 0.2F),
			(double)blockPos.getY(),
			(double)((float)blockPos.getZ() + 0.2F),
			(double)((float)(blockPos.getX() + 1) - 0.2F),
			(double)((float)(blockPos.getY() + 1) - 0.2F),
			(double)((float)(blockPos.getZ() + 1) - 0.2F)
		);
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		switch (blockRotation) {
			case CLOCKWISE_180:
				switch ((RailShape)blockState.get(SHAPE)) {
					case ASCENDING_EAST:
						return blockState.with(SHAPE, RailShape.ASCENDING_WEST);
					case ASCENDING_WEST:
						return blockState.with(SHAPE, RailShape.ASCENDING_EAST);
					case ASCENDING_NORTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_NORTH);
					case SOUTH_EAST:
						return blockState.with(SHAPE, RailShape.NORTH_WEST);
					case SOUTH_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_EAST);
					case NORTH_WEST:
						return blockState.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_EAST:
						return blockState.with(SHAPE, RailShape.SOUTH_WEST);
				}
			case COUNTERCLOCKWISE_90:
				switch ((RailShape)blockState.get(SHAPE)) {
					case ASCENDING_EAST:
						return blockState.with(SHAPE, RailShape.ASCENDING_NORTH);
					case ASCENDING_WEST:
						return blockState.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_NORTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_WEST);
					case ASCENDING_SOUTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_EAST);
					case SOUTH_EAST:
						return blockState.with(SHAPE, RailShape.NORTH_EAST);
					case SOUTH_WEST:
						return blockState.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_WEST:
						return blockState.with(SHAPE, RailShape.SOUTH_WEST);
					case NORTH_EAST:
						return blockState.with(SHAPE, RailShape.NORTH_WEST);
					case NORTH_SOUTH:
						return blockState.with(SHAPE, RailShape.EAST_WEST);
					case EAST_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_SOUTH);
				}
			case CLOCKWISE_90:
				switch ((RailShape)blockState.get(SHAPE)) {
					case ASCENDING_EAST:
						return blockState.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_WEST:
						return blockState.with(SHAPE, RailShape.ASCENDING_NORTH);
					case ASCENDING_NORTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_EAST);
					case ASCENDING_SOUTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_WEST);
					case SOUTH_EAST:
						return blockState.with(SHAPE, RailShape.SOUTH_WEST);
					case SOUTH_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_WEST);
					case NORTH_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_EAST);
					case NORTH_EAST:
						return blockState.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_SOUTH:
						return blockState.with(SHAPE, RailShape.EAST_WEST);
					case EAST_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_SOUTH);
				}
			default:
				return blockState;
		}
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		RailShape railShape = blockState.get(SHAPE);
		switch (blockMirror) {
			case LEFT_RIGHT:
				switch (railShape) {
					case ASCENDING_NORTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_NORTH);
					case SOUTH_EAST:
						return blockState.with(SHAPE, RailShape.NORTH_EAST);
					case SOUTH_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_WEST);
					case NORTH_WEST:
						return blockState.with(SHAPE, RailShape.SOUTH_WEST);
					case NORTH_EAST:
						return blockState.with(SHAPE, RailShape.SOUTH_EAST);
					default:
						return super.mirror(blockState, blockMirror);
				}
			case FRONT_BACK:
				switch (railShape) {
					case ASCENDING_EAST:
						return blockState.with(SHAPE, RailShape.ASCENDING_WEST);
					case ASCENDING_WEST:
						return blockState.with(SHAPE, RailShape.ASCENDING_EAST);
					case ASCENDING_NORTH:
					case ASCENDING_SOUTH:
					default:
						break;
					case SOUTH_EAST:
						return blockState.with(SHAPE, RailShape.SOUTH_WEST);
					case SOUTH_WEST:
						return blockState.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_EAST);
					case NORTH_EAST:
						return blockState.with(SHAPE, RailShape.NORTH_WEST);
				}
		}

		return super.mirror(blockState, blockMirror);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(SHAPE, POWERED);
	}
}
