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
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class DetectorRailBlock extends AbstractRailBlock {
	public static final EnumProperty<RailShape> SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
	public static final BooleanProperty POWERED = Properties.POWERED;

	public DetectorRailBlock(Block.Settings settings) {
		super(true, settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(POWERED, Boolean.valueOf(false)).with(SHAPE, RailShape.field_12665));
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
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
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient && (Boolean)blockState.get(POWERED)) {
			this.updatePoweredStatus(world, blockPos, blockState);
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
			return direction == Direction.field_11036 ? 15 : 0;
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
			world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(true)), 3);
			this.updateNearbyRails(world, blockPos, blockState, true);
			world.updateNeighborsAlways(blockPos, this);
			world.updateNeighborsAlways(blockPos.down(), this);
			world.scheduleBlockRender(blockPos);
		}

		if (!bl2 && bl) {
			world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(false)), 3);
			this.updateNearbyRails(world, blockPos, blockState, false);
			world.updateNeighborsAlways(blockPos, this);
			world.updateNeighborsAlways(blockPos.down(), this);
			world.scheduleBlockRender(blockPos);
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

	private BoundingBox getCartDetectionBox(BlockPos blockPos) {
		float f = 0.2F;
		return new BoundingBox(
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
			case field_11464:
				switch ((RailShape)blockState.get(SHAPE)) {
					case field_12667:
						return blockState.with(SHAPE, RailShape.field_12666);
					case field_12666:
						return blockState.with(SHAPE, RailShape.field_12667);
					case field_12670:
						return blockState.with(SHAPE, RailShape.field_12668);
					case field_12668:
						return blockState.with(SHAPE, RailShape.field_12670);
					case field_12664:
						return blockState.with(SHAPE, RailShape.field_12672);
					case field_12671:
						return blockState.with(SHAPE, RailShape.field_12663);
					case field_12672:
						return blockState.with(SHAPE, RailShape.field_12664);
					case field_12663:
						return blockState.with(SHAPE, RailShape.field_12671);
				}
			case field_11465:
				switch ((RailShape)blockState.get(SHAPE)) {
					case field_12667:
						return blockState.with(SHAPE, RailShape.field_12670);
					case field_12666:
						return blockState.with(SHAPE, RailShape.field_12668);
					case field_12670:
						return blockState.with(SHAPE, RailShape.field_12666);
					case field_12668:
						return blockState.with(SHAPE, RailShape.field_12667);
					case field_12664:
						return blockState.with(SHAPE, RailShape.field_12663);
					case field_12671:
						return blockState.with(SHAPE, RailShape.field_12664);
					case field_12672:
						return blockState.with(SHAPE, RailShape.field_12671);
					case field_12663:
						return blockState.with(SHAPE, RailShape.field_12672);
					case field_12665:
						return blockState.with(SHAPE, RailShape.field_12674);
					case field_12674:
						return blockState.with(SHAPE, RailShape.field_12665);
				}
			case field_11463:
				switch ((RailShape)blockState.get(SHAPE)) {
					case field_12667:
						return blockState.with(SHAPE, RailShape.field_12668);
					case field_12666:
						return blockState.with(SHAPE, RailShape.field_12670);
					case field_12670:
						return blockState.with(SHAPE, RailShape.field_12667);
					case field_12668:
						return blockState.with(SHAPE, RailShape.field_12666);
					case field_12664:
						return blockState.with(SHAPE, RailShape.field_12671);
					case field_12671:
						return blockState.with(SHAPE, RailShape.field_12672);
					case field_12672:
						return blockState.with(SHAPE, RailShape.field_12663);
					case field_12663:
						return blockState.with(SHAPE, RailShape.field_12664);
					case field_12665:
						return blockState.with(SHAPE, RailShape.field_12674);
					case field_12674:
						return blockState.with(SHAPE, RailShape.field_12665);
				}
			default:
				return blockState;
		}
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		RailShape railShape = blockState.get(SHAPE);
		switch (blockMirror) {
			case field_11300:
				switch (railShape) {
					case field_12670:
						return blockState.with(SHAPE, RailShape.field_12668);
					case field_12668:
						return blockState.with(SHAPE, RailShape.field_12670);
					case field_12664:
						return blockState.with(SHAPE, RailShape.field_12663);
					case field_12671:
						return blockState.with(SHAPE, RailShape.field_12672);
					case field_12672:
						return blockState.with(SHAPE, RailShape.field_12671);
					case field_12663:
						return blockState.with(SHAPE, RailShape.field_12664);
					default:
						return super.mirror(blockState, blockMirror);
				}
			case field_11301:
				switch (railShape) {
					case field_12667:
						return blockState.with(SHAPE, RailShape.field_12666);
					case field_12666:
						return blockState.with(SHAPE, RailShape.field_12667);
					case field_12670:
					case field_12668:
					default:
						break;
					case field_12664:
						return blockState.with(SHAPE, RailShape.field_12671);
					case field_12671:
						return blockState.with(SHAPE, RailShape.field_12664);
					case field_12672:
						return blockState.with(SHAPE, RailShape.field_12663);
					case field_12663:
						return blockState.with(SHAPE, RailShape.field_12672);
				}
		}

		return super.mirror(blockState, blockMirror);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(SHAPE, POWERED);
	}
}
