/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import org.jetbrains.annotations.Nullable;

public class EntityTrackerUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int id;
    @Nullable
    private final List<DataTracker.Entry<?>> trackedValues;

    public EntityTrackerUpdateS2CPacket(int id, DataTracker tracker, boolean forceUpdateAll) {
        this.id = id;
        if (forceUpdateAll) {
            this.trackedValues = tracker.getAllEntries();
            tracker.clearDirty();
        } else {
            this.trackedValues = tracker.getDirtyEntries();
        }
    }

    public EntityTrackerUpdateS2CPacket(PacketByteBuf buf) {
        this.id = buf.readVarInt();
        this.trackedValues = DataTracker.deserializePacket(buf);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.id);
        DataTracker.entriesToPacket(this.trackedValues, buf);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntityTrackerUpdate(this);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public List<DataTracker.Entry<?>> getTrackedValues() {
        return this.trackedValues;
    }

    @Environment(value=EnvType.CLIENT)
    public int id() {
        return this.id;
    }
}

