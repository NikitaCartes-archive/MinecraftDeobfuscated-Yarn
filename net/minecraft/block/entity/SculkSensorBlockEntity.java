/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.VibrationListener;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class SculkSensorBlockEntity
extends BlockEntity
implements VibrationListener.Callback {
    private static final Logger field_38236 = LogUtils.getLogger();
    private VibrationListener listener;
    private int lastVibrationFrequency;

    public SculkSensorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.SCULK_SENSOR, pos, state);
        this.listener = new VibrationListener(new BlockPositionSource(this.pos), ((SculkSensorBlock)state.getBlock()).getRange(), this, null, 0.0f, 0);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.lastVibrationFrequency = nbt.getInt("last_vibration_frequency");
        if (nbt.contains("listener", NbtElement.COMPOUND_TYPE)) {
            VibrationListener.createCodec(this).parse(new Dynamic<NbtCompound>(NbtOps.INSTANCE, nbt.getCompound("listener"))).resultOrPartial(field_38236::error).ifPresent(listener -> {
                this.listener = listener;
            });
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("last_vibration_frequency", this.lastVibrationFrequency);
        VibrationListener.createCodec(this).encodeStart(NbtOps.INSTANCE, this.listener).resultOrPartial(field_38236::error).ifPresent(listenerNbt -> nbt.put("listener", (NbtElement)listenerNbt));
    }

    public VibrationListener getEventListener() {
        return this.listener;
    }

    public int getLastVibrationFrequency() {
        return this.lastVibrationFrequency;
    }

    @Override
    public boolean triggersAvoidCriterion() {
        return true;
    }

    @Override
    public boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable GameEvent.Emitter emitter) {
        if (this.isRemoved() || pos.equals(this.getPos()) && (event == GameEvent.BLOCK_DESTROY || event == GameEvent.BLOCK_PLACE)) {
            return false;
        }
        return SculkSensorBlock.isInactive(this.getCachedState());
    }

    @Override
    public void accept(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, float distance) {
        BlockState blockState = this.getCachedState();
        if (SculkSensorBlock.isInactive(blockState)) {
            this.lastVibrationFrequency = SculkSensorBlock.FREQUENCIES.getInt(event);
            SculkSensorBlock.setActive(entity, world, this.pos, blockState, SculkSensorBlockEntity.getPower(distance, listener.getRange()));
        }
    }

    @Override
    public void onListen() {
        this.markDirty();
    }

    public static int getPower(float distance, int range) {
        double d = (double)distance / (double)range;
        return Math.max(1, 15 - MathHelper.floor(d * 15.0));
    }

    public void setLastVibrationFrequency(int lastVibrationFrequency) {
        this.lastVibrationFrequency = lastVibrationFrequency;
    }
}

