/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.SculkSensorListener;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class SculkShriekerBlockEntity
extends BlockEntity
implements SculkSensorListener.Callback {
    private static final Logger field_38237 = LogUtils.getLogger();
    private static final int RANGE = 8;
    private SculkSensorListener vibrationListener;

    public SculkShriekerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.SCULK_SHRIEKER, pos, state);
        this.vibrationListener = new SculkSensorListener(new BlockPositionSource(this.pos), 8, this, null, 0, 0);
    }

    public SculkSensorListener getVibrationListener() {
        return this.vibrationListener;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("listener", 10)) {
            SculkSensorListener.createCodec(this).parse(new Dynamic<NbtCompound>(NbtOps.INSTANCE, nbt.getCompound("listener"))).resultOrPartial(field_38237::error).ifPresent(vibrationListener -> {
                this.vibrationListener = vibrationListener;
            });
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        SculkSensorListener.createCodec(this).encodeStart(NbtOps.INSTANCE, this.vibrationListener).resultOrPartial(field_38237::error).ifPresent(nbtElement -> nbt.put("listener", (NbtElement)nbtElement));
    }

    @Override
    public boolean canAccept(GameEvent gameEvent, @Nullable Entity entity) {
        return true;
    }

    @Override
    public boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity) {
        return event == GameEvent.SCULK_SENSOR_TENDRILS_CLICKING && SculkShriekerBlock.canShriek(world, this.getPos(), this.getCachedState());
    }

    @Override
    public void accept(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, int delay) {
        SculkShriekerBlock.shriek(world, this.getCachedState(), this.getPos());
    }
}

