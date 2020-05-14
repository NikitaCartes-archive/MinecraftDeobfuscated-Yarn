package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.PositionImpl;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DispenserBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = FacingBlock.FACING;
	public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
	private static final Map<Item, DispenserBehavior> BEHAVIORS = Util.make(
		new Object2ObjectOpenHashMap<>(), object2ObjectOpenHashMap -> object2ObjectOpenHashMap.defaultReturnValue(new ItemDispenserBehavior())
	);

	public static void registerBehavior(ItemConvertible provider, DispenserBehavior behavior) {
		BEHAVIORS.put(provider.asItem(), behavior);
	}

	protected DispenserBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TRIGGERED, Boolean.valueOf(false)));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof DispenserBlockEntity) {
				player.openHandledScreen((DispenserBlockEntity)blockEntity);
				if (blockEntity instanceof DropperBlockEntity) {
					player.incrementStat(Stats.INSPECT_DROPPER);
				} else {
					player.incrementStat(Stats.INSPECT_DISPENSER);
				}
			}

			return ActionResult.SUCCESS;
		}
	}

	protected void dispense(World world, BlockPos pos) {
		BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, pos);
		DispenserBlockEntity dispenserBlockEntity = blockPointerImpl.getBlockEntity();
		int i = dispenserBlockEntity.chooseNonEmptySlot();
		if (i < 0) {
			world.syncWorldEvent(1001, pos, 0);
		} else {
			ItemStack itemStack = dispenserBlockEntity.getStack(i);
			DispenserBehavior dispenserBehavior = this.getBehaviorForItem(itemStack);
			if (dispenserBehavior != DispenserBehavior.NOOP) {
				dispenserBlockEntity.setStack(i, dispenserBehavior.dispense(blockPointerImpl, itemStack));
			}
		}
	}

	protected DispenserBehavior getBehaviorForItem(ItemStack stack) {
		return (DispenserBehavior)BEHAVIORS.get(stack.getItem());
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		boolean bl = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
		boolean bl2 = (Boolean)state.get(TRIGGERED);
		if (bl && !bl2) {
			world.getBlockTickScheduler().schedule(pos, this, 4);
			world.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(true)), 4);
		} else if (!bl && bl2) {
			world.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(false)), 4);
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.dispense(world, pos);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new DispenserBlockEntity();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof DispenserBlockEntity) {
				((DispenserBlockEntity)blockEntity).setCustomName(itemStack.getName());
			}
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof DispenserBlockEntity) {
				ItemScatterer.spawn(world, pos, (DispenserBlockEntity)blockEntity);
				world.updateComparators(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, notify);
		}
	}

	public static Position getOutputLocation(BlockPointer pointer) {
		Direction direction = pointer.getBlockState().get(FACING);
		double d = pointer.getX() + 0.7 * (double)direction.getOffsetX();
		double e = pointer.getY() + 0.7 * (double)direction.getOffsetY();
		double f = pointer.getZ() + 0.7 * (double)direction.getOffsetZ();
		return new PositionImpl(d, e, f);
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
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
		builder.add(FACING, TRIGGERED);
	}
}
