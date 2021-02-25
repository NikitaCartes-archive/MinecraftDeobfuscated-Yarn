/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;

public class WorldEventS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int eventId;
    private final BlockPos pos;
    private final int data;
    private final boolean global;

    public WorldEventS2CPacket(int eventId, BlockPos pos, int data, boolean global) {
        this.eventId = eventId;
        this.pos = pos.toImmutable();
        this.data = data;
        this.global = global;
    }

    public WorldEventS2CPacket(PacketByteBuf buf) {
        this.eventId = buf.readInt();
        this.pos = buf.readBlockPos();
        this.data = buf.readInt();
        this.global = buf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.eventId);
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.data);
        buf.writeBoolean(this.global);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onWorldEvent(this);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isGlobal() {
        return this.global;
    }

    @Environment(value=EnvType.CLIENT)
    public int getEventId() {
        return this.eventId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getData() {
        return this.data;
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }
}

