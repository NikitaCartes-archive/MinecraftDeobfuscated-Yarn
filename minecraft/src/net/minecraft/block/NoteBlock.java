package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.enums.NoteBlockInstrument;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;

public class NoteBlock extends Block {
	public static final MapCodec<NoteBlock> CODEC = createCodec(NoteBlock::new);
	public static final EnumProperty<NoteBlockInstrument> INSTRUMENT = Properties.INSTRUMENT;
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
			this.stateManager.getDefaultState().with(INSTRUMENT, NoteBlockInstrument.HARP).with(NOTE, Integer.valueOf(0)).with(POWERED, Boolean.valueOf(false))
		);
	}

	private BlockState getStateWithInstrument(WorldView world, BlockPos pos, BlockState state) {
		NoteBlockInstrument noteBlockInstrument = world.getBlockState(pos.up()).getInstrument();
		if (noteBlockInstrument.isNotBaseBlock()) {
			return state.with(INSTRUMENT, noteBlockInstrument);
		} else {
			NoteBlockInstrument noteBlockInstrument2 = world.getBlockState(pos.down()).getInstrument();
			NoteBlockInstrument noteBlockInstrument3 = noteBlockInstrument2.isNotBaseBlock() ? NoteBlockInstrument.HARP : noteBlockInstrument2;
			return state.with(INSTRUMENT, noteBlockInstrument3);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getStateWithInstrument(ctx.getWorld(), ctx.getBlockPos(), this.getDefaultState());
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state,
		WorldView world,
		ScheduledTickView tickView,
		BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		Random random
	) {
		boolean bl = direction.getAxis() == Direction.Axis.Y;
		return bl
			? this.getStateWithInstrument(world, pos, state)
			: super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
		boolean bl = world.isReceivingRedstonePower(pos);
		if (bl != (Boolean)state.get(POWERED)) {
			if (bl) {
				this.playNote(null, state, world, pos);
			}

			world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(bl)), Block.NOTIFY_ALL);
		}
	}

	private void playNote(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {
		if (((NoteBlockInstrument)state.get(INSTRUMENT)).isNotBaseBlock() || world.getBlockState(pos.up()).isAir()) {
			world.addSyncedBlockEvent(pos, this, 0, 0);
			world.emitGameEvent(entity, GameEvent.NOTE_BLOCK_PLAY, pos);
		}
	}

	@Override
	protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return (ActionResult)(stack.isIn(ItemTags.NOTEBLOCK_TOP_INSTRUMENTS) && hit.getSide() == Direction.UP
			? ActionResult.PASS
			: super.onUseWithItem(stack, state, world, pos, player, hand, hit));
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (!world.isClient) {
			state = state.cycle(NOTE);
			world.setBlockState(pos, state, Block.NOTIFY_ALL);
			this.playNote(player, state, world, pos);
			player.incrementStat(Stats.TUNE_NOTEBLOCK);
		}

		return ActionResult.SUCCESS;
	}

	@Override
	protected void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		if (!world.isClient) {
			this.playNote(player, state, world, pos);
			player.incrementStat(Stats.PLAY_NOTEBLOCK);
		}
	}

	public static float getNotePitch(int note) {
		return (float)Math.pow(2.0, (double)(note - 12) / 12.0);
	}

	@Override
	protected boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		NoteBlockInstrument noteBlockInstrument = state.get(INSTRUMENT);
		float f;
		if (noteBlockInstrument.canBePitched()) {
			int i = (Integer)state.get(NOTE);
			f = getNotePitch(i);
			world.addParticle(ParticleTypes.NOTE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)i / 24.0, 0.0, 0.0);
		} else {
			f = 1.0F;
		}

		RegistryEntry<SoundEvent> registryEntry;
		if (noteBlockInstrument.hasCustomSound()) {
			Identifier identifier = this.getCustomSound(world, pos);
			if (identifier == null) {
				return false;
			}

			registryEntry = RegistryEntry.of(SoundEvent.of(identifier));
		} else {
			registryEntry = noteBlockInstrument.getSound();
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
