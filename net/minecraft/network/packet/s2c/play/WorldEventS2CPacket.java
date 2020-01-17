/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class WorldEventS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int eventId;
    private BlockPos pos;
    private int data;
    private boolean global;

    public WorldEventS2CPacket() {
    }

    public WorldEventS2CPacket(int eventId, BlockPos pos, int data, boolean global) {
        this.eventId = eventId;
        this.pos = pos.toImmutable();
        this.data = data;
        this.global = global;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.eventId = buf.readInt();
        this.pos = buf.readBlockPos();
        this.data = buf.readInt();
        this.global = buf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
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
    public int getEffectData() {
        return this.data;
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }
}

