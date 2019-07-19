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

public class KeepAliveS2CPacket
implements Packet<ClientPlayPacketListener> {
    private long id;

    public KeepAliveS2CPacket() {
    }

    public KeepAliveS2CPacket(long l) {
        this.id = l;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onKeepAlive(this);
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.id = packetByteBuf.readLong();
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeLong(this.id);
    }

    @Environment(value=EnvType.CLIENT)
    public long getId() {
        return this.id;
    }
}

