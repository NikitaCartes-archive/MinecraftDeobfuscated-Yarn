package net.minecraft.block;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.registry.tag.BlockTags;
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
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;

public class SculkSensorBlock extends BlockWithEntity implements Waterloggable {
	public static final MapCodec<SculkSensorBlock> CODEC = createCodec(SculkSensorBlock::new);
	public static final int field_31239 = 30;
	public static final int field_44607 = 10;
	public static final EnumProperty<SculkSensorPhase> SCULK_SENSOR_PHASE = Properties.SCULK_SENSOR_PHASE;
	public static final IntProperty POWER = Properties.POWER;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape OUTLINE_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	private static final float[] RESONATION_NOTE_PITCHES = Util.make(new float[16], frequency -> {
		int[] is = new int[]{0, 0, 2, 4, 6, 7, 9, 10, 12, 14, 15, 18, 19, 21, 22, 24};

		for (int i = 0; i < 16; i++) {
			frequency[i] = NoteBlock.getNotePitch(is[i]);
		}
	});

	@Override
	public MapCodec<? extends SculkSensorBlock> getCodec() {
		return CODEC;
	}

	public SculkSensorBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(SCULK_SENSOR_PHASE, SculkSensorPhase.INACTIVE)
				.with(POWER, Integer.valueOf(0))
				.with(WATERLOGGED, Boolean.valueOf(false))
		);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos blockPos = ctx.getBlockPos();
		FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
		return this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (getPhase(state) != SculkSensorPhase.ACTIVE) {
			if (getPhase(state) == SculkSensorPhase.COOLDOWN) {
				world.setBlockState(pos, state.with(SCULK_SENSOR_PHASE, SculkSensorPhase.INACTIVE), Block.NOTIFY_ALL);
				if (!(Boolean)state.get(WATERLOGGED)) {
					world.playSound(null, pos, SoundEvents.BLOCK_SCULK_SENSOR_CLICKING_STOP, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.2F + 0.8F);
				}
			}
		} else {
			setCooldown(world, pos, state);
		}
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		if (!world.isClient()
			&& isInactive(state)
			&& entity.getType() != EntityType.WARDEN
			&& world.getBlockEntity(pos) instanceof SculkSensorBlockEntity sculkSensorBlockEntity
			&& world instanceof ServerWorld serverWorld
			&& sculkSensorBlockEntity.getVibrationCallback().accepts(serverWorld, pos, GameEvent.STEP, GameEvent.Emitter.of(state))) {
			sculkSensorBlockEntity.getEventListener().forceListen(serverWorld, GameEvent.STEP, GameEvent.Emitter.of(entity), entity.getPos());
		}

		super.onSteppedOn(world, pos, state, entity);
	}

	@Override
	protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!world.isClient() && !state.isOf(oldState.getBlock())) {
			if ((Integer)state.get(POWER) > 0 && !world.getBlockTickScheduler().isQueued(pos, this)) {
				world.setBlockState(pos, state.with(POWER, Integer.valueOf(0)), Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
			}
		}
	}

	@Override
	protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			if (getPhase(state) == SculkSensorPhase.ACTIVE) {
				updateNeighbors(world, pos, state);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	private static void updateNeighbors(World world, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		world.updateNeighborsAlways(pos, block);
		world.updateNeighborsAlways(pos.down(), block);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SculkSensorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return !world.isClient
			? validateTicker(
				type,
				BlockEntityType.SCULK_SENSOR,
				(worldx, pos, statex, blockEntity) -> Vibrations.Ticker.tick(worldx, blockEntity.getVibrationListenerData(), blockEntity.getVibrationCallback())
			)
			: null;
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return OUTLINE_SHAPE;
	}

	@Override
	protected boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return (Integer)state.get(POWER);
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return direction == Direction.UP ? state.getWeakRedstonePower(world, pos, direction) : 0;
	}

	public static SculkSensorPhase getPhase(BlockState state) {
		return state.get(SCULK_SENSOR_PHASE);
	}

	public static boolean isInactive(BlockState state) {
		return getPhase(state) == SculkSensorPhase.INACTIVE;
	}

	public static void setCooldown(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.with(SCULK_SENSOR_PHASE, SculkSensorPhase.COOLDOWN).with(POWER, Integer.valueOf(0)), Block.NOTIFY_ALL);
		world.scheduleBlockTick(pos, state.getBlock(), 10);
		updateNeighbors(world, pos, state);
	}

	@VisibleForTesting
	public int getCooldownTime() {
		return 30;
	}

	public void setActive(@Nullable Entity sourceEntity, World world, BlockPos pos, BlockState state, int power, int frequency) {
		world.setBlockState(pos, state.with(SCULK_SENSOR_PHASE, SculkSensorPhase.ACTIVE).with(POWER, Integer.valueOf(power)), Block.NOTIFY_ALL);
		world.scheduleBlockTick(pos, state.getBlock(), this.getCooldownTime());
		updateNeighbors(world, pos, state);
		tryResonate(sourceEntity, world, pos, frequency);
		world.emitGameEvent(sourceEntity, GameEvent.SCULK_SENSOR_TENDRILS_CLICKING, pos);
		if (!(Boolean)state.get(WATERLOGGED)) {
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
	}

	public static void tryResonate(@Nullable Entity sourceEntity, World world, BlockPos pos, int frequency) {
		for (Direction direction : Direction.values()) {
			BlockPos blockPos = pos.offset(direction);
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.isIn(BlockTags.VIBRATION_RESONATORS)) {
				world.emitGameEvent(Vibrations.getResonation(frequency), blockPos, GameEvent.Emitter.of(sourceEntity, blockState));
				float f = RESONATION_NOTE_PITCHES[frequency];
				world.playSound(null, blockPos, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.BLOCKS, 1.0F, f);
			}
		}
	}

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
	protected boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		if (world.getBlockEntity(pos) instanceof SculkSensorBlockEntity sculkSensorBlockEntity) {
			return getPhase(state) == SculkSensorPhase.ACTIVE ? sculkSensorBlockEntity.getLastVibrationFrequency() : 0;
		} else {
			return 0;
		}
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	@Override
	protected boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Override
	protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
		super.onStacksDropped(state, world, pos, tool, dropExperience);
		if (dropExperience) {
			this.dropExperienceWhenMined(world, pos, tool, ConstantIntProvider.create(5));
		}
	}
}
