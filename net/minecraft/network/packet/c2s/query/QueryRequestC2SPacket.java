/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.query;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerQueryPacketListener;

public class QueryRequestC2SPacket
implements Packet<ServerQueryPacketListener> {
    public QueryRequestC2SPacket() {
    }

    public QueryRequestC2SPacket(PacketByteBuf buf) {
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    @Override
    public void apply(ServerQueryPacketListener serverQueryPacketListener) {
        serverQueryPacketListener.onRequest(this);
    }
}

