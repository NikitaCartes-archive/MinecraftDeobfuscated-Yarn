/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
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
import org.jetbrains.annotations.Nullable;

public class NoteBlock
extends Block {
    public static final EnumProperty<Instrument> INSTRUMENT = Properties.INSTRUMENT;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final IntProperty NOTE = Properties.NOTE;
    public static final int field_41678 = 3;

    public NoteBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(INSTRUMENT, Instrument.HARP)).with(NOTE, 0)).with(POWERED, false));
    }

    private static boolean areMobHeadSoundsEnabled(WorldAccess world) {
        return world.getEnabledFeatures().contains(FeatureFlags.UPDATE_1_20);
    }

    private BlockState getStateWithInstrument(WorldAccess world, BlockPos pos, BlockState state) {
        if (NoteBlock.areMobHeadSoundsEnabled(world)) {
            BlockState blockState = world.getBlockState(pos.up());
            return (BlockState)state.with(INSTRUMENT, Instrument.fromAboveState(blockState).orElseGet(() -> Instrument.fromBelowState(world.getBlockState(pos.down()))));
        }
        return (BlockState)state.with(INSTRUMENT, Instrument.fromBelowState(world.getBlockState(pos.down())));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getStateWithInstrument(ctx.getWorld(), ctx.getBlockPos(), this.getDefaultState());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        boolean bl;
        boolean bl2 = NoteBlock.areMobHeadSoundsEnabled(world) ? direction.getAxis() == Direction.Axis.Y : (bl = direction == Direction.DOWN);
        if (bl) {
            return this.getStateWithInstrument(world, pos, state);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean bl = world.isReceivingRedstonePower(pos);
        if (bl != state.get(POWERED)) {
            if (bl) {
                this.playNote(null, state, world, pos);
            }
            world.setBlockState(pos, (BlockState)state.with(POWERED, bl), Block.NOTIFY_ALL);
        }
    }

    private void playNote(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {
        if (!state.get(INSTRUMENT).shouldRequireAirAbove() || world.getBlockState(pos.up()).isAir()) {
            world.addSyncedBlockEvent(pos, this, 0, 0);
            world.emitGameEvent(entity, GameEvent.NOTE_BLOCK_PLAY, pos);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        state = (BlockState)state.cycle(NOTE);
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
        this.playNote(player, state, world, pos);
        player.incrementStat(Stats.TUNE_NOTEBLOCK);
        return ActionResult.CONSUME;
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (world.isClient) {
            return;
        }
        this.playNote(player, state, world, pos);
        player.incrementStat(Stats.PLAY_NOTEBLOCK);
    }

    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        RegistryEntry<SoundEvent> registryEntry;
        float f;
        Instrument instrument = state.get(INSTRUMENT);
        if (instrument.shouldSpawnNoteParticles()) {
            int i = state.get(NOTE);
            f = (float)Math.pow(2.0, (double)(i - 12) / 12.0);
            world.addParticle(ParticleTypes.NOTE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)i / 24.0, 0.0, 0.0);
        } else {
            f = 1.0f;
        }
        if (instrument.hasCustomSound()) {
            Identifier identifier = this.getCustomSound(world, pos);
            if (identifier == null) {
                return false;
            }
            registryEntry = RegistryEntry.of(SoundEvent.of(identifier));
        } else {
            registryEntry = instrument.getSound();
        }
        world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, registryEntry, SoundCategory.RECORDS, 3.0f, f, world.random.nextLong());
        return true;
    }

    @Nullable
    private Identifier getCustomSound(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos.up());
        if (blockEntity instanceof SkullBlockEntity) {
            SkullBlockEntity skullBlockEntity = (SkullBlockEntity)blockEntity;
            return skullBlockEntity.getNoteBlockSound();
        }
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(INSTRUMENT, POWERED, NOTE);
    }
}

