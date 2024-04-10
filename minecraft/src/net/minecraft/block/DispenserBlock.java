package net.minecraft.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
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
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;

public class DispenserBlock extends BlockWithEntity {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final MapCodec<DispenserBlock> CODEC = createCodec(DispenserBlock::new);
	public static final DirectionProperty FACING = FacingBlock.FACING;
	public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
	private static final ItemDispenserBehavior DEFAULT_BEHAVIOR = new ItemDispenserBehavior();
	public static final Map<Item, DispenserBehavior> BEHAVIORS = Util.make(new Object2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(DEFAULT_BEHAVIOR));
	private static final int SCHEDULED_TICK_DELAY = 4;

	@Override
	public MapCodec<? extends DispenserBlock> getCodec() {
		return CODEC;
	}

	public static void registerBehavior(ItemConvertible provider, DispenserBehavior behavior) {
		BEHAVIORS.put(provider.asItem(), behavior);
	}

	public static void registerProjectileBehavior(ItemConvertible projectile) {
		BEHAVIORS.put(projectile.asItem(), new ProjectileDispenserBehavior(projectile.asItem()));
	}

	protected DispenserBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TRIGGERED, Boolean.valueOf(false)));
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
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

			return ActionResult.CONSUME;
		}
	}

	protected void dispense(ServerWorld world, BlockState state, BlockPos pos) {
		DispenserBlockEntity dispenserBlockEntity = (DispenserBlockEntity)world.getBlockEntity(pos, BlockEntityType.DISPENSER).orElse(null);
		if (dispenserBlockEntity == null) {
			LOGGER.warn("Ignoring dispensing attempt for Dispenser without matching block entity at {}", pos);
		} else {
			BlockPointer blockPointer = new BlockPointer(world, pos, state, dispenserBlockEntity);
			int i = dispenserBlockEntity.chooseNonEmptySlot(world.random);
			if (i < 0) {
				world.syncWorldEvent(WorldEvents.DISPENSER_FAILS, pos, 0);
				world.emitGameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Emitter.of(dispenserBlockEntity.getCachedState()));
			} else {
				ItemStack itemStack = dispenserBlockEntity.getStack(i);
				DispenserBehavior dispenserBehavior = this.getBehaviorForItem(world, itemStack);
				if (dispenserBehavior != DispenserBehavior.NOOP) {
					dispenserBlockEntity.setStack(i, dispenserBehavior.dispense(blockPointer, itemStack));
				}
			}
		}
	}

	protected DispenserBehavior getBehaviorForItem(World world, ItemStack stack) {
		return (DispenserBehavior)(!stack.isItemEnabled(world.getEnabledFeatures()) ? DEFAULT_BEHAVIOR : (DispenserBehavior)BEHAVIORS.get(stack.getItem()));
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		boolean bl = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
		boolean bl2 = (Boolean)state.get(TRIGGERED);
		if (bl && !bl2) {
			world.scheduleBlockTick(pos, this, 4);
			world.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
		} else if (!bl && bl2) {
			world.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(false)), Block.NOTIFY_LISTENERS);
		}
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.dispense(world, state, pos);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new DispenserBlockEntity(pos, state);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
	}

	@Override
	protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		ItemScatterer.onStateReplaced(state, newState, world, pos);
		super.onStateReplaced(state, world, pos, newState, moved);
	}

	public static Position getOutputLocation(BlockPointer pointer) {
		return getOutputLocation(pointer, 0.7, Vec3d.ZERO);
	}

	public static Position getOutputLocation(BlockPointer pointer, double facingOffset, Vec3d constantOffset) {
		Direction direction = pointer.state().get(FACING);
		return pointer.centerPos()
			.add(
				facingOffset * (double)direction.getOffsetX() + constantOffset.getX(),
				facingOffset * (double)direction.getOffsetY() + constantOffset.getY(),
				facingOffset * (double)direction.getOffsetZ() + constantOffset.getZ()
			);
	}

	@Override
	protected boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, TRIGGERED);
	}
}
