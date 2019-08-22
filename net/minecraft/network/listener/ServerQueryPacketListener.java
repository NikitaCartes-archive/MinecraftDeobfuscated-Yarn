/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.listener;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.packet.QueryPingC2SPacket;
import net.minecraft.server.network.packet.QueryRequestC2SPacket;

public interface ServerQueryPacketListener
extends PacketListener {
    public void onPing(QueryPingC2SPacket var1);

    public void onRequest(QueryRequestC2SPacket var1);
}

