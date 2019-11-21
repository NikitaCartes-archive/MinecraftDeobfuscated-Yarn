/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.util.PacketByteBuf;

public class QueryRequestC2SPacket
implements Packet<ServerQueryPacketListener> {
    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
    }

    @Override
    public void apply(ServerQueryPacketListener serverQueryPacketListener) {
        serverQueryPacketListener.onRequest(this);
    }
}

