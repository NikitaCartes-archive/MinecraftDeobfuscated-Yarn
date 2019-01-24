package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.sortme.ItemScatterer;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.PositionImpl;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class DispenserBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = FacingBlock.FACING;
	public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
	private static final Map<Item, DispenserBehavior> BEHAVIORS = SystemUtil.consume(
		new Object2ObjectOpenHashMap<>(), object2ObjectOpenHashMap -> object2ObjectOpenHashMap.defaultReturnValue(new ItemDispenserBehavior())
	);

	public static void registerBehavior(ItemProvider itemProvider, DispenserBehavior dispenserBehavior) {
		BEHAVIORS.put(itemProvider.getItem(), dispenserBehavior);
	}

	protected DispenserBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.NORTH).with(TRIGGERED, Boolean.valueOf(false)));
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 4;
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof DispenserBlockEntity) {
				playerEntity.openContainer((DispenserBlockEntity)blockEntity);
				if (blockEntity instanceof DropperBlockEntity) {
					playerEntity.increaseStat(Stats.field_15367);
				} else {
					playerEntity.increaseStat(Stats.field_15371);
				}
			}

			return true;
		}
	}

	protected void dispense(World world, BlockPos blockPos) {
		BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, blockPos);
		DispenserBlockEntity dispenserBlockEntity = blockPointerImpl.getBlockEntity();
		int i = dispenserBlockEntity.chooseNonEmptySlot();
		if (i < 0) {
			world.fireWorldEvent(1001, blockPos, 0);
		} else {
			ItemStack itemStack = dispenserBlockEntity.getInvStack(i);
			DispenserBehavior dispenserBehavior = this.getBehaviorForItem(itemStack);
			if (dispenserBehavior != DispenserBehavior.NOOP) {
				dispenserBlockEntity.setInvStack(i, dispenserBehavior.dispense(blockPointerImpl, itemStack));
			}
		}
	}

	protected DispenserBehavior getBehaviorForItem(ItemStack itemStack) {
		return (DispenserBehavior)BEHAVIORS.get(itemStack.getItem());
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		boolean bl = world.isReceivingRedstonePower(blockPos) || world.isReceivingRedstonePower(blockPos.up());
		boolean bl2 = (Boolean)blockState.get(TRIGGERED);
		if (bl && !bl2) {
			world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
			world.setBlockState(blockPos, blockState.with(TRIGGERED, Boolean.valueOf(true)), 4);
		} else if (!bl && bl2) {
			world.setBlockState(blockPos, blockState.with(TRIGGERED, Boolean.valueOf(false)), 4);
		}
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			this.dispense(world, blockPos);
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new DispenserBlockEntity();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(FACING, itemPlacementContext.getPlayerFacing().getOpposite());
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasDisplayName()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof DispenserBlockEntity) {
				((DispenserBlockEntity)blockEntity).setCustomName(itemStack.getDisplayName());
			}
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof DispenserBlockEntity) {
				ItemScatterer.spawn(world, blockPos, (DispenserBlockEntity)blockEntity);
				world.updateHorizontalAdjacent(blockPos, this);
			}

			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
		}
	}

	public static Position getOutputLocation(BlockPointer blockPointer) {
		Direction direction = blockPointer.getBlockState().get(FACING);
		double d = blockPointer.getX() + 0.7 * (double)direction.getOffsetX();
		double e = blockPointer.getY() + 0.7 * (double)direction.getOffsetY();
		double f = blockPointer.getZ() + 0.7 * (double)direction.getOffsetZ();
		return new PositionImpl(d, e, f);
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		return Container.calculateComparatorOutput(world.getBlockEntity(blockPos));
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11458;
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
		builder.with(FACING, TRIGGERED);
	}
}
