/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class KeepAliveC2SPacket
implements Packet<ServerPlayPacketListener> {
    private final long id;

    public KeepAliveC2SPacket(long id) {
        this.id = id;
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onKeepAlive(this);
    }

    public KeepAliveC2SPacket(PacketByteBuf buf) {
        this.id = buf.readLong();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeLong(this.id);
    }

    public long getId() {
        return this.id;
    }
}

