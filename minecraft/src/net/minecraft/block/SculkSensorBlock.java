package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;

public class SculkSensorBlock extends BlockWithEntity implements Waterloggable {
	public static final Object2IntMap<GameEvent> FREQUENCIES = Object2IntMaps.unmodifiable(Util.make(new Object2IntOpenHashMap<>(), map -> {
		map.put(GameEvent.STEP, 1);
		map.put(GameEvent.FLAP, 2);
		map.put(GameEvent.SWIM, 3);
		map.put(GameEvent.ELYTRA_FREE_FALL, 4);
		map.put(GameEvent.HIT_GROUND, 5);
		map.put(GameEvent.SPLASH, 6);
		map.put(GameEvent.WOLF_SHAKING, 6);
		map.put(GameEvent.PROJECTILE_SHOOT, 7);
		map.put(GameEvent.PROJECTILE_LAND, 8);
		map.put(GameEvent.EATING_START, 7);
		map.put(GameEvent.EATING_FINISH, 8);
		map.put(GameEvent.ENTITY_HIT, 9);
		map.put(GameEvent.ARMOR_STAND_ADD_ITEM, 9);
		map.put(GameEvent.BLOCK_OPEN, 11);
		map.put(GameEvent.BLOCK_CLOSE, 10);
		map.put(GameEvent.BLOCK_SWITCH, 11);
		map.put(GameEvent.BLOCK_UNSWITCH, 10);
		map.put(GameEvent.BLOCK_PRESS, 11);
		map.put(GameEvent.BLOCK_UNPRESS, 10);
		map.put(GameEvent.BLOCK_ATTACH, 11);
		map.put(GameEvent.BLOCK_DETACH, 10);
		map.put(GameEvent.CONTAINER_OPEN, 11);
		map.put(GameEvent.CONTAINER_CLOSE, 10);
		map.put(GameEvent.DISPENSE_FAIL, 10);
		map.put(GameEvent.FLINT_AND_STEEL_USE, 12);
		map.put(GameEvent.BLOCK_PLACE, 12);
		map.put(GameEvent.BLOCK_DESTROY, 13);
		map.put(GameEvent.FLUID_PLACE, 12);
		map.put(GameEvent.FLUID_PICKUP, 13);
		map.put(GameEvent.FISHING_ROD_CAST, 15);
		map.put(GameEvent.FISHING_ROD_REEL_IN, 14);
		map.put(GameEvent.PISTON_EXTEND, 15);
		map.put(GameEvent.PISTON_CONTRACT, 14);
		map.put(GameEvent.EXPLODE, 15);
		map.put(GameEvent.LIGHTNING_STRIKE, 15);
	}));
	public static final EnumProperty<SculkSensorPhase> SCULK_SENSOR_PHASE = Properties.SCULK_SENSOR_PHASE;
	public static final IntProperty POWER = Properties.POWER;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape OUTLINE_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	private final int range;

	public SculkSensorBlock(AbstractBlock.Settings settings, int range) {
		super(settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(SCULK_SENSOR_PHASE, SculkSensorPhase.COOLDOWN)
				.with(POWER, Integer.valueOf(0))
				.with(WATERLOGGED, Boolean.valueOf(false))
		);
		this.range = range;
	}

	public int getRange() {
		return this.range;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos blockPos = ctx.getBlockPos();
		FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
		return this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (getPhase(state) != SculkSensorPhase.ACTIVE) {
			if (getPhase(state) == SculkSensorPhase.COOLDOWN) {
				world.setBlockState(pos, state.with(SCULK_SENSOR_PHASE, SculkSensorPhase.INACTIVE), 3);
			}
		} else {
			setCooldown(world, pos, state);
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!world.isClient() && !state.isOf(oldState.getBlock())) {
			if ((Integer)state.get(POWER) > 0 && !world.getBlockTickScheduler().isScheduled(pos, this)) {
				world.setBlockState(pos, state.with(POWER, Integer.valueOf(0)), 18);
			}

			world.getBlockTickScheduler().schedule(new BlockPos(pos), state.getBlock(), 1);
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			if (getPhase(state) == SculkSensorPhase.ACTIVE) {
				updateNeighbors(world, pos);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	private static void updateNeighbors(World world, BlockPos pos) {
		world.updateNeighborsAlways(pos, Blocks.SCULK_SENSOR);
		world.updateNeighborsAlways(pos.offset(Direction.UP.getOpposite()), Blocks.SCULK_SENSOR);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SculkSensorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> GameEventListener getGameEventListener(World world, T blockEntity) {
		return blockEntity instanceof SculkSensorBlockEntity ? ((SculkSensorBlockEntity)blockEntity).getEventListener() : null;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return !world.isClient
			? checkType(
				type,
				BlockEntityType.SCULK_SENSOR,
				(worldx, blockPos, blockState, sculkSensorBlockEntity) -> sculkSensorBlockEntity.getEventListener().method_32964(worldx)
			)
			: null;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return OUTLINE_SHAPE;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return (Integer)state.get(POWER);
	}

	public static SculkSensorPhase getPhase(BlockState state) {
		return state.get(SCULK_SENSOR_PHASE);
	}

	public static boolean isInactive(BlockState state) {
		return getPhase(state) == SculkSensorPhase.INACTIVE;
	}

	public static void setCooldown(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.with(SCULK_SENSOR_PHASE, SculkSensorPhase.COOLDOWN).with(POWER, Integer.valueOf(0)), 3);
		world.getBlockTickScheduler().schedule(new BlockPos(pos), state.getBlock(), 1);
		world.playSound(null, pos, SoundEvents.BLOCK_SCULK_SENSOR_CLICKING_STOP, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.2F + 0.8F);
		updateNeighbors(world, pos);
	}

	public static void setActive(World world, BlockPos pos, BlockState state, int power) {
		world.setBlockState(pos, state.with(SCULK_SENSOR_PHASE, SculkSensorPhase.ACTIVE).with(POWER, Integer.valueOf(power)), 3);
		world.getBlockTickScheduler().schedule(new BlockPos(pos), state.getBlock(), 40);
		updateNeighbors(world, pos);
		world.playSound(
			null,
			(double)pos.getX() + 0.5,
			(double)pos.getY() + 0.5,
			(double)pos.getZ() + 0.5,
			SoundEvents.BLOCK_SCULK_SENSOR_CLICKING,
			SoundCategory.BLOCKS,
			1.0F,
			world.random.nextFloat() * 0.2F + 0.8F
		);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (getPhase(state) == SculkSensorPhase.ACTIVE) {
			Direction direction = Direction.random(random);
			if (direction != Direction.UP && direction != Direction.DOWN) {
				double d = (double)pos.getX() + 0.5 + (direction.getOffsetX() == 0 ? 0.5 - random.nextDouble() : (double)direction.getOffsetX() * 0.6);
				double e = (double)pos.getY() + 0.25;
				double f = (double)pos.getZ() + 0.5 + (direction.getOffsetZ() == 0 ? 0.5 - random.nextDouble() : (double)direction.getOffsetZ() * 0.6);
				double g = (double)random.nextFloat() * 0.04;
				world.addParticle(DustColorTransitionParticleEffect.DEFAULT, d, e, f, 0.0, g, 0.0);
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(SCULK_SENSOR_PHASE, POWER, WATERLOGGED);
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof SculkSensorBlockEntity) {
			SculkSensorBlockEntity sculkSensorBlockEntity = (SculkSensorBlockEntity)blockEntity;
			return getPhase(state) == SculkSensorPhase.ACTIVE ? sculkSensorBlockEntity.getLastVibrationFrequency() : 0;
		} else {
			return 0;
		}
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}
}
