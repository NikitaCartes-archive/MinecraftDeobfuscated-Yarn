package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.container.Container;
import net.minecraft.container.DoubleLockableContainer;
import net.minecraft.container.LockableContainer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sortme.ItemScatterer;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ChestBlock extends BlockWithEntity implements Waterloggable {
	public static final DirectionProperty FACING = HorizontalFacingBlock.field_11177;
	public static final EnumProperty<ChestType> field_10770 = Properties.CHEST_TYPE;
	public static final BooleanProperty field_10772 = Properties.WATERLOGGED;
	protected static final VoxelShape field_10767 = Block.createCubeShape(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
	protected static final VoxelShape field_10771 = Block.createCubeShape(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
	protected static final VoxelShape field_10773 = Block.createCubeShape(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
	protected static final VoxelShape field_10769 = Block.createCubeShape(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
	protected static final VoxelShape field_10774 = Block.createCubeShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);

	protected ChestBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory.getDefaultState().with(FACING, Direction.NORTH).with(field_10770, ChestType.field_12569).with(field_10772, Boolean.valueOf(false))
		);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasBlockEntityBreakingRender(BlockState blockState) {
		return true;
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11456;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if ((Boolean)blockState.get(field_10772)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.method_15789(iWorld));
		}

		if (blockState2.getBlock() == this && direction.getAxis().isHorizontal()) {
			ChestType chestType = blockState2.get(field_10770);
			if (blockState.get(field_10770) == ChestType.field_12569
				&& chestType != ChestType.field_12569
				&& blockState.get(FACING) == blockState2.get(FACING)
				&& method_9758(blockState2) == direction.getOpposite()) {
				return blockState.with(field_10770, chestType.method_11824());
			}
		} else if (method_9758(blockState) == direction) {
			return blockState.with(field_10770, ChestType.field_12569);
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		if (blockState.get(field_10770) == ChestType.field_12569) {
			return field_10774;
		} else {
			switch (method_9758(blockState)) {
				case NORTH:
				default:
					return field_10767;
				case SOUTH:
					return field_10771;
				case WEST:
					return field_10773;
				case EAST:
					return field_10769;
			}
		}
	}

	public static Direction method_9758(BlockState blockState) {
		Direction direction = blockState.get(FACING);
		return blockState.get(field_10770) == ChestType.field_12574 ? direction.rotateYClockwise() : direction.rotateYCounterclockwise();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		ChestType chestType = ChestType.field_12569;
		Direction direction = itemPlacementContext.getPlayerHorizontalFacing().getOpposite();
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getPos());
		boolean bl = itemPlacementContext.isPlayerSneaking();
		Direction direction2 = itemPlacementContext.getFacing();
		if (direction2.getAxis().isHorizontal() && bl) {
			Direction direction3 = this.method_9753(itemPlacementContext, direction2.getOpposite());
			if (direction3 != null && direction3.getAxis() != direction2.getAxis()) {
				direction = direction3;
				chestType = direction3.rotateYCounterclockwise() == direction2.getOpposite() ? ChestType.field_12571 : ChestType.field_12574;
			}
		}

		if (chestType == ChestType.field_12569 && !bl) {
			if (direction == this.method_9753(itemPlacementContext, direction.rotateYClockwise())) {
				chestType = ChestType.field_12574;
			} else if (direction == this.method_9753(itemPlacementContext, direction.rotateYCounterclockwise())) {
				chestType = ChestType.field_12571;
			}
		}

		return this.getDefaultState().with(FACING, direction).with(field_10770, chestType).with(field_10772, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.get(field_10772) ? Fluids.WATER.getState(false) : super.getFluidState(blockState);
	}

	@Nullable
	private Direction method_9753(ItemPlacementContext itemPlacementContext, Direction direction) {
		BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getPos().offset(direction));
		return blockState.getBlock() == this && blockState.get(field_10770) == ChestType.field_12569 ? blockState.get(FACING) : null;
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasDisplayName()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof ChestBlockEntity) {
				((ChestBlockEntity)blockEntity).setCustomName(itemStack.getDisplayName());
			}
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof Inventory) {
				ItemScatterer.spawn(world, blockPos, (Inventory)blockEntity);
				world.updateHorizontalAdjacent(blockPos, this);
			}

			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public boolean activate(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		if (world.isClient) {
			return true;
		} else {
			LockableContainer lockableContainer = this.getContainer(blockState, world, blockPos, false);
			if (lockableContainer != null) {
				playerEntity.openInventory(lockableContainer);
				playerEntity.incrementStat(this.method_9755());
			}

			return true;
		}
	}

	protected Stat<Identifier> method_9755() {
		return Stats.field_15419.method_14956(Stats.field_15395);
	}

	@Nullable
	public LockableContainer getContainer(BlockState blockState, World world, BlockPos blockPos, boolean bl) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (!(blockEntity instanceof ChestBlockEntity)) {
			return null;
		} else if (!bl && this.isChestBlocked(world, blockPos)) {
			return null;
		} else {
			LockableContainer lockableContainer = (ChestBlockEntity)blockEntity;
			ChestType chestType = blockState.get(field_10770);
			if (chestType == ChestType.field_12569) {
				return lockableContainer;
			} else {
				BlockPos blockPos2 = blockPos.offset(method_9758(blockState));
				BlockState blockState2 = world.getBlockState(blockPos2);
				if (blockState2.getBlock() == this) {
					ChestType chestType2 = blockState2.get(field_10770);
					if (chestType2 != ChestType.field_12569 && chestType != chestType2 && blockState2.get(FACING) == blockState.get(FACING)) {
						if (!bl && this.isChestBlocked(world, blockPos2)) {
							return null;
						}

						BlockEntity blockEntity2 = world.getBlockEntity(blockPos2);
						if (blockEntity2 instanceof ChestBlockEntity) {
							LockableContainer lockableContainer2 = chestType == ChestType.field_12571 ? lockableContainer : (LockableContainer)blockEntity2;
							LockableContainer lockableContainer3 = chestType == ChestType.field_12571 ? (LockableContainer)blockEntity2 : lockableContainer;
							lockableContainer = new DoubleLockableContainer(new TranslatableTextComponent("container.chestDouble"), lockableContainer2, lockableContainer3);
						}
					}
				}

				return lockableContainer;
			}
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new ChestBlockEntity();
	}

	private boolean isChestBlocked(World world, BlockPos blockPos) {
		return this.hasBlockOnTop(world, blockPos) || this.hasOcelotOnTop(world, blockPos);
	}

	private boolean hasBlockOnTop(BlockView blockView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		return blockView.getBlockState(blockPos2).isSimpleFullBlock(blockView, blockPos2);
	}

	private boolean hasOcelotOnTop(World world, BlockPos blockPos) {
		List<CatEntity> list = world.getVisibleEntities(
			CatEntity.class,
			new BoundingBox(
				(double)blockPos.getX(),
				(double)(blockPos.getY() + 1),
				(double)blockPos.getZ(),
				(double)(blockPos.getX() + 1),
				(double)(blockPos.getY() + 2),
				(double)(blockPos.getZ() + 1)
			)
		);
		if (!list.isEmpty()) {
			for (CatEntity catEntity : list) {
				if (catEntity.isSitting()) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		return Container.calculateComparatorOutput(this.getContainer(blockState, world, blockPos, false));
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(FACING, rotation.method_10503(blockState.get(FACING)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.applyRotation(mirror.getRotation(blockState.get(FACING)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(FACING, field_10770, field_10772);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		return false;
	}
}
