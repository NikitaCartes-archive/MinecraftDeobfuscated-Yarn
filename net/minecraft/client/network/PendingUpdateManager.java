/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class PendingUpdateManager
implements AutoCloseable {
    private final Long2ObjectOpenHashMap<PendingUpdate> blockPosToPendingUpdate = new Long2ObjectOpenHashMap();
    private int sequence;
    private boolean pendingSequence;

    public void addPendingUpdate(BlockPos pos, BlockState state, ClientPlayerEntity player) {
        this.blockPosToPendingUpdate.compute(pos.asLong(), (posLong, pendingUpdate) -> {
            if (pendingUpdate != null) {
                return pendingUpdate.withSequence(this.sequence);
            }
            return new PendingUpdate(this.sequence, state, player.getPos());
        });
    }

    public boolean hasPendingUpdate(BlockPos pos, BlockState state) {
        PendingUpdate pendingUpdate = this.blockPosToPendingUpdate.get(pos.asLong());
        if (pendingUpdate == null) {
            return false;
        }
        pendingUpdate.setBlockState(state);
        return true;
    }

    public void processPendingUpdates(int maxProcessableSequence, ClientWorld world) {
        Iterator objectIterator = this.blockPosToPendingUpdate.long2ObjectEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)objectIterator.next();
            PendingUpdate pendingUpdate = (PendingUpdate)entry.getValue();
            if (pendingUpdate.sequence > maxProcessableSequence) continue;
            BlockPos blockPos = BlockPos.fromLong(entry.getLongKey());
            objectIterator.remove();
            world.processPendingUpdate(blockPos, pendingUpdate.blockState, pendingUpdate.playerPos);
        }
    }

    public PendingUpdateManager incrementSequence() {
        ++this.sequence;
        this.pendingSequence = true;
        return this;
    }

    @Override
    public void close() {
        this.pendingSequence = false;
    }

    public int getSequence() {
        return this.sequence;
    }

    public boolean hasPendingSequence() {
        return this.pendingSequence;
    }

    @Environment(value=EnvType.CLIENT)
    static class PendingUpdate {
        final Vec3d playerPos;
        int sequence;
        BlockState blockState;

        PendingUpdate(int sequence, BlockState blockState, Vec3d playerPos) {
            this.sequence = sequence;
            this.blockState = blockState;
            this.playerPos = playerPos;
        }

        PendingUpdate withSequence(int sequence) {
            this.sequence = sequence;
            return this;
        }

        void setBlockState(BlockState state) {
            this.blockState = state;
        }
    }
}

