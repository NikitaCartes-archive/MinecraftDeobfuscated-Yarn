package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.ScreenHandler;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DetectorRailBlock extends AbstractRailBlock {
	public static final MapCodec<DetectorRailBlock> CODEC = createCodec(DetectorRailBlock::new);
	public static final EnumProperty<RailShape> SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
	public static final BooleanProperty POWERED = Properties.POWERED;
	private static final int SCHEDULED_TICK_DELAY = 20;

	@Override
	public MapCodec<DetectorRailBlock> getCodec() {
		return CODEC;
	}

	public DetectorRailBlock(AbstractBlock.Settings settings) {
		super(true, settings);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(POWERED, Boolean.valueOf(false)).with(SHAPE, RailShape.NORTH_SOUTH).with(WATERLOGGED, Boolean.valueOf(false))
		);
	}

	@Override
	protected boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient) {
			if (!(Boolean)state.get(POWERED)) {
				this.updatePoweredStatus(world, pos, state);
			}
		}
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if ((Boolean)state.get(POWERED)) {
			this.updatePoweredStatus(world, pos, state);
		}
	}

	@Override
	protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (!(Boolean)state.get(POWERED)) {
			return 0;
		} else {
			return direction == Direction.UP ? 15 : 0;
		}
	}

	private void updatePoweredStatus(World world, BlockPos pos, BlockState state) {
		if (this.canPlaceAt(state, world, pos)) {
			boolean bl = (Boolean)state.get(POWERED);
			boolean bl2 = false;
			List<AbstractMinecartEntity> list = this.getCarts(world, pos, AbstractMinecartEntity.class, entity -> true);
			if (!list.isEmpty()) {
				bl2 = true;
			}

			if (bl2 && !bl) {
				BlockState blockState = state.with(POWERED, Boolean.valueOf(true));
				world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
				this.updateNearbyRails(world, pos, blockState, true);
				world.updateNeighborsAlways(pos, this);
				world.updateNeighborsAlways(pos.down(), this);
				world.scheduleBlockRerenderIfNeeded(pos, state, blockState);
			}

			if (!bl2 && bl) {
				BlockState blockState = state.with(POWERED, Boolean.valueOf(false));
				world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
				this.updateNearbyRails(world, pos, blockState, false);
				world.updateNeighborsAlways(pos, this);
				world.updateNeighborsAlways(pos.down(), this);
				world.scheduleBlockRerenderIfNeeded(pos, state, blockState);
			}

			if (bl2) {
				world.scheduleBlockTick(pos, this, 20);
			}

			world.updateComparators(pos, this);
		}
	}

	protected void updateNearbyRails(World world, BlockPos pos, BlockState state, boolean unpowering) {
		RailPlacementHelper railPlacementHelper = new RailPlacementHelper(world, pos, state);

		for (BlockPos blockPos : railPlacementHelper.getNeighbors()) {
			BlockState blockState = world.getBlockState(blockPos);
			world.updateNeighbor(blockState, blockPos, blockState.getBlock(), pos, false);
		}
	}

	@Override
	protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!oldState.isOf(state.getBlock())) {
			BlockState blockState = this.updateCurves(state, world, pos, notify);
			this.updatePoweredStatus(world, pos, blockState);
		}
	}

	@Override
	public Property<RailShape> getShapeProperty() {
		return SHAPE;
	}

	@Override
	protected boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		if ((Boolean)state.get(POWERED)) {
			List<CommandBlockMinecartEntity> list = this.getCarts(world, pos, CommandBlockMinecartEntity.class, cart -> true);
			if (!list.isEmpty()) {
				return ((CommandBlockMinecartEntity)list.get(0)).getCommandExecutor().getSuccessCount();
			}

			List<AbstractMinecartEntity> list2 = this.getCarts(world, pos, AbstractMinecartEntity.class, EntityPredicates.VALID_INVENTORIES);
			if (!list2.isEmpty()) {
				return ScreenHandler.calculateComparatorOutput((Inventory)list2.get(0));
			}
		}

		return 0;
	}

	private <T extends AbstractMinecartEntity> List<T> getCarts(World world, BlockPos pos, Class<T> entityClass, Predicate<Entity> entityPredicate) {
		return world.getEntitiesByClass(entityClass, this.getCartDetectionBox(pos), entityPredicate);
	}

	private Box getCartDetectionBox(BlockPos pos) {
		double d = 0.2;
		return new Box(
			(double)pos.getX() + 0.2,
			(double)pos.getY(),
			(double)pos.getZ() + 0.2,
			(double)(pos.getX() + 1) - 0.2,
			(double)(pos.getY() + 1) - 0.2,
			(double)(pos.getZ() + 1) - 0.2
		);
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				switch ((RailShape)state.get(SHAPE)) {
					case ASCENDING_EAST:
						return state.with(SHAPE, RailShape.ASCENDING_WEST);
					case ASCENDING_WEST:
						return state.with(SHAPE, RailShape.ASCENDING_EAST);
					case ASCENDING_NORTH:
						return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return state.with(SHAPE, RailShape.ASCENDING_NORTH);
					case SOUTH_EAST:
						return state.with(SHAPE, RailShape.NORTH_WEST);
					case SOUTH_WEST:
						return state.with(SHAPE, RailShape.NORTH_EAST);
					case NORTH_WEST:
						return state.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_EAST:
						return state.with(SHAPE, RailShape.SOUTH_WEST);
				}
			case COUNTERCLOCKWISE_90:
				switch ((RailShape)state.get(SHAPE)) {
					case ASCENDING_EAST:
						return state.with(SHAPE, RailShape.ASCENDING_NORTH);
					case ASCENDING_WEST:
						return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_NORTH:
						return state.with(SHAPE, RailShape.ASCENDING_WEST);
					case ASCENDING_SOUTH:
						return state.with(SHAPE, RailShape.ASCENDING_EAST);
					case SOUTH_EAST:
						return state.with(SHAPE, RailShape.NORTH_EAST);
					case SOUTH_WEST:
						return state.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_WEST:
						return state.with(SHAPE, RailShape.SOUTH_WEST);
					case NORTH_EAST:
						return state.with(SHAPE, RailShape.NORTH_WEST);
					case NORTH_SOUTH:
						return state.with(SHAPE, RailShape.EAST_WEST);
					case EAST_WEST:
						return state.with(SHAPE, RailShape.NORTH_SOUTH);
				}
			case CLOCKWISE_90:
				switch ((RailShape)state.get(SHAPE)) {
					case ASCENDING_EAST:
						return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_WEST:
						return state.with(SHAPE, RailShape.ASCENDING_NORTH);
					case ASCENDING_NORTH:
						return state.with(SHAPE, RailShape.ASCENDING_EAST);
					case ASCENDING_SOUTH:
						return state.with(SHAPE, RailShape.ASCENDING_WEST);
					case SOUTH_EAST:
						return state.with(SHAPE, RailShape.SOUTH_WEST);
					case SOUTH_WEST:
						return state.with(SHAPE, RailShape.NORTH_WEST);
					case NORTH_WEST:
						return state.with(SHAPE, RailShape.NORTH_EAST);
					case NORTH_EAST:
						return state.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_SOUTH:
						return state.with(SHAPE, RailShape.EAST_WEST);
					case EAST_WEST:
						return state.with(SHAPE, RailShape.NORTH_SOUTH);
				}
			default:
				return state;
		}
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		RailShape railShape = state.get(SHAPE);
		switch (mirror) {
			case LEFT_RIGHT:
				switch (railShape) {
					case ASCENDING_NORTH:
						return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return state.with(SHAPE, RailShape.ASCENDING_NORTH);
					case SOUTH_EAST:
						return state.with(SHAPE, RailShape.NORTH_EAST);
					case SOUTH_WEST:
						return state.with(SHAPE, RailShape.NORTH_WEST);
					case NORTH_WEST:
						return state.with(SHAPE, RailShape.SOUTH_WEST);
					case NORTH_EAST:
						return state.with(SHAPE, RailShape.SOUTH_EAST);
					default:
						return super.mirror(state, mirror);
				}
			case FRONT_BACK:
				switch (railShape) {
					case ASCENDING_EAST:
						return state.with(SHAPE, RailShape.ASCENDING_WEST);
					case ASCENDING_WEST:
						return state.with(SHAPE, RailShape.ASCENDING_EAST);
					case ASCENDING_NORTH:
					case ASCENDING_SOUTH:
					default:
						break;
					case SOUTH_EAST:
						return state.with(SHAPE, RailShape.SOUTH_WEST);
					case SOUTH_WEST:
						return state.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_WEST:
						return state.with(SHAPE, RailShape.NORTH_EAST);
					case NORTH_EAST:
						return state.with(SHAPE, RailShape.NORTH_WEST);
				}
		}

		return super.mirror(state, mirror);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(SHAPE, POWERED, WATERLOGGED);
	}
}
