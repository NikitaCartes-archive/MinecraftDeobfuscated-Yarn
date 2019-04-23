/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.listener;

import net.minecraft.client.network.packet.QueryPongS2CPacket;
import net.minecraft.client.network.packet.QueryResponseS2CPacket;
import net.minecraft.network.listener.PacketListener;

public interface ClientQueryPacketListener
extends PacketListener {
    public void onResponse(QueryResponseS2CPacket var1);

    public void onPong(QueryPongS2CPacket var1);
}

