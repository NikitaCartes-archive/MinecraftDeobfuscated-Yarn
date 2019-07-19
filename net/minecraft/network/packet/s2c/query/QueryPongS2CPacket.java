/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.query;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.util.PacketByteBuf;

public class QueryPongS2CPacket
implements Packet<ClientQueryPacketListener> {
    private long startTime;

    public QueryPongS2CPacket() {
    }

    public QueryPongS2CPacket(long l) {
        this.startTime = l;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.startTime = packetByteBuf.readLong();
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeLong(this.startTime);
    }

    @Override
    public void apply(ClientQueryPacketListener clientQueryPacketListener) {
        clientQueryPacketListener.onPong(this);
    }
}

