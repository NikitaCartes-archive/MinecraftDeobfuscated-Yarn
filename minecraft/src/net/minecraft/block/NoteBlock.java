package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.class_9062;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class NoteBlock extends Block {
	public static final MapCodec<NoteBlock> CODEC = createCodec(NoteBlock::new);
	public static final EnumProperty<Instrument> INSTRUMENT = Properties.INSTRUMENT;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final IntProperty NOTE = Properties.NOTE;
	public static final int field_41678 = 3;

	@Override
	public MapCodec<NoteBlock> getCodec() {
		return CODEC;
	}

	public NoteBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(INSTRUMENT, Instrument.HARP).with(NOTE, Integer.valueOf(0)).with(POWERED, Boolean.valueOf(false))
		);
	}

	private BlockState getStateWithInstrument(WorldAccess world, BlockPos pos, BlockState state) {
		Instrument instrument = world.getBlockState(pos.up()).getInstrument();
		if (instrument.isNotBaseBlock()) {
			return state.with(INSTRUMENT, instrument);
		} else {
			Instrument instrument2 = world.getBlockState(pos.down()).getInstrument();
			Instrument instrument3 = instrument2.isNotBaseBlock() ? Instrument.HARP : instrument2;
			return state.with(INSTRUMENT, instrument3);
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
		boolean bl = direction.getAxis() == Direction.Axis.Y;
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
		if (((Instrument)state.get(INSTRUMENT)).isNotBaseBlock() || world.getBlockState(pos.up()).isAir()) {
			world.addSyncedBlockEvent(pos, this, 0, 0);
			world.emitGameEvent(entity, GameEvent.NOTE_BLOCK_PLAY, pos);
		}
	}

	@Override
	public class_9062 method_55765(
		ItemStack itemStack, BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult
	) {
		return itemStack.isIn(ItemTags.NOTEBLOCK_TOP_INSTRUMENTS) && blockHitResult.getSide() == Direction.UP
			? class_9062.SKIP_DEFAULT_BLOCK_INTERACTION
			: super.method_55765(itemStack, blockState, world, blockPos, playerEntity, hand, blockHitResult);
	}

	@Override
	public ActionResult method_55766(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			blockState = blockState.cycle(NOTE);
			world.setBlockState(blockPos, blockState, Block.NOTIFY_ALL);
			this.playNote(playerEntity, blockState, world, blockPos);
			playerEntity.incrementStat(Stats.TUNE_NOTEBLOCK);
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

	public static float getNotePitch(int note) {
		return (float)Math.pow(2.0, (double)(note - 12) / 12.0);
	}

	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		Instrument instrument = state.get(INSTRUMENT);
		float f;
		if (instrument.shouldSpawnNoteParticles()) {
			int i = (Integer)state.get(NOTE);
			f = getNotePitch(i);
			world.addParticle(ParticleTypes.NOTE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)i / 24.0, 0.0, 0.0);
		} else {
			f = 1.0F;
		}

		RegistryEntry<SoundEvent> registryEntry;
		if (instrument.hasCustomSound()) {
			Identifier identifier = this.getCustomSound(world, pos);
			if (identifier == null) {
				return false;
			}

			registryEntry = RegistryEntry.of(SoundEvent.of(identifier));
		} else {
			registryEntry = instrument.getSound();
		}

		world.playSound(
			null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, registryEntry, SoundCategory.RECORDS, 3.0F, f, world.random.nextLong()
		);
		return true;
	}

	@Nullable
	private Identifier getCustomSound(World world, BlockPos pos) {
		return world.getBlockEntity(pos.up()) instanceof SkullBlockEntity skullBlockEntity ? skullBlockEntity.getNoteBlockSound() : null;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(INSTRUMENT, POWERED, NOTE);
	}
}
