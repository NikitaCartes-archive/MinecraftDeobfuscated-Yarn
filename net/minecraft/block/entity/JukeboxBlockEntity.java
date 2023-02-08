/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class JukeboxBlockEntity
extends BlockEntity
implements Clearable {
    private ItemStack record = ItemStack.EMPTY;
    private int ticksThisSecond;
    private long tickCount;
    private long recordStartTick;
    private boolean isPlaying;

    public JukeboxBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.JUKEBOX, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("RecordItem", NbtElement.COMPOUND_TYPE)) {
            this.setRecord(ItemStack.fromNbt(nbt.getCompound("RecordItem")));
        }
        this.isPlaying = nbt.getBoolean("IsPlaying");
        this.recordStartTick = nbt.getLong("RecordStartTick");
        this.tickCount = nbt.getLong("TickCount");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.getRecord().isEmpty()) {
            nbt.put("RecordItem", this.getRecord().writeNbt(new NbtCompound()));
        }
        nbt.putBoolean("IsPlaying", this.isPlaying);
        nbt.putLong("RecordStartTick", this.recordStartTick);
        nbt.putLong("TickCount", this.tickCount);
    }

    public ItemStack getRecord() {
        return this.record;
    }

    public void setRecord(ItemStack stack) {
        this.record = stack;
        this.markDirty();
    }

    public void startPlaying() {
        this.recordStartTick = this.tickCount;
        this.isPlaying = true;
    }

    @Override
    public void clear() {
        this.setRecord(ItemStack.EMPTY);
        this.isPlaying = false;
    }

    public static void tick(World world, BlockPos pos, BlockState state, JukeboxBlockEntity blockEntity) {
        Item item;
        ++blockEntity.ticksThisSecond;
        if (JukeboxBlockEntity.isPlayingRecord(state, blockEntity) && (item = blockEntity.getRecord().getItem()) instanceof MusicDiscItem) {
            MusicDiscItem musicDiscItem = (MusicDiscItem)item;
            if (JukeboxBlockEntity.isSongFinished(blockEntity, musicDiscItem)) {
                world.emitGameEvent(GameEvent.JUKEBOX_STOP_PLAY, pos, GameEvent.Emitter.of(state));
                blockEntity.isPlaying = false;
            } else if (JukeboxBlockEntity.hasSecondPassed(blockEntity)) {
                blockEntity.ticksThisSecond = 0;
                world.emitGameEvent(GameEvent.JUKEBOX_PLAY, pos, GameEvent.Emitter.of(state));
                JukeboxBlockEntity.spawnNoteParticle(world, pos);
            }
        }
        ++blockEntity.tickCount;
    }

    private static void spawnNoteParticle(World world, BlockPos pos) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            Vec3d vec3d = Vec3d.ofBottomCenter(pos).add(0.0, 1.2f, 0.0);
            float f = (float)world.getRandom().nextInt(4) / 24.0f;
            serverWorld.spawnParticles(ParticleTypes.NOTE, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0, f, 0.0, 0.0, 1.0);
        }
    }

    private static boolean isPlayingRecord(BlockState state, JukeboxBlockEntity blockEntity) {
        return state.get(JukeboxBlock.HAS_RECORD) != false && blockEntity.isPlaying;
    }

    private static boolean isSongFinished(JukeboxBlockEntity blockEntity, MusicDiscItem musicDisc) {
        return blockEntity.tickCount >= blockEntity.recordStartTick + (long)musicDisc.getSongLengthInTicks();
    }

    private static boolean hasSecondPassed(JukeboxBlockEntity blockEntity) {
        return blockEntity.ticksThisSecond >= 20;
    }
}

