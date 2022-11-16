package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class NoteBlock extends Block {
	public static final EnumProperty<Instrument> INSTRUMENT = Properties.INSTRUMENT;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final IntProperty NOTE = Properties.NOTE;

	public NoteBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(INSTRUMENT, Instrument.HARP).with(NOTE, Integer.valueOf(0)).with(POWERED, Boolean.valueOf(false))
		);
	}

	private static boolean areMobHeadSoundsEnabled(WorldAccess world) {
		return world.getEnabledFeatures().contains(FeatureFlags.UPDATE_1_20);
	}

	private BlockState getStateWithInstrument(WorldAccess world, BlockPos pos, BlockState state) {
		if (areMobHeadSoundsEnabled(world)) {
			BlockState blockState = world.getBlockState(pos.up());
			return state.with(INSTRUMENT, (Instrument)Instrument.fromAboveState(blockState).orElseGet(() -> Instrument.fromBelowState(world.getBlockState(pos.down()))));
		} else {
			return state.with(INSTRUMENT, Instrument.fromBelowState(world.getBlockState(pos.down())));
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getStateWithInstrument(ctx.getWorld(), ctx.getBlockPos(), this.getDefaultState());
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		boolean bl = areMobHeadSoundsEnabled(world) ? direction.getAxis() == Direction.Axis.Y : direction == Direction.DOWN;
		return bl ? this.getStateWithInstrument(world, pos, state) : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		boolean bl = world.isReceivingRedstonePower(pos);
		if (bl != (Boolean)state.get(POWERED)) {
			if (bl) {
				this.playNote(null, state, world, pos);
			}

			world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(bl)), Block.NOTIFY_ALL);
		}
	}

	private void playNote(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {
		if (this.isInstrumentMobHead(state) || world.getBlockState(pos.up()).isAir()) {
			world.addSyncedBlockEvent(pos, this, 0, 0);
			world.emitGameEvent(entity, GameEvent.NOTE_BLOCK_PLAY, pos);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			state = state.cycle(NOTE);
			world.setBlockState(pos, state, Block.NOTIFY_ALL);
			this.playNote(player, state, world, pos);
			player.incrementStat(Stats.TUNE_NOTEBLOCK);
			return ActionResult.CONSUME;
		}
	}

	@Override
	public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		if (!world.isClient) {
			this.playNote(player, state, world, pos);
			player.incrementStat(Stats.PLAY_NOTEBLOCK);
		}
	}

	private boolean isInstrumentMobHead(BlockState state) {
		return ((Instrument)state.get(INSTRUMENT)).isMobHead();
	}

	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		float f;
		if (!this.isInstrumentMobHead(state)) {
			int i = (Integer)state.get(NOTE);
			f = (float)Math.pow(2.0, (double)(i - 12) / 12.0);
			world.addParticle(ParticleTypes.NOTE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)i / 24.0, 0.0, 0.0);
		} else {
			f = 1.0F;
		}

		world.playSound(null, pos, ((Instrument)state.get(INSTRUMENT)).getSound(), SoundCategory.RECORDS, 3.0F, f);
		return true;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(INSTRUMENT, POWERED, NOTE);
	}
}
