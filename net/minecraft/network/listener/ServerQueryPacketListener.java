/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.listener;

import net.minecraft.class_6857;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;

public interface ServerQueryPacketListener
extends class_6857 {
    public void onPing(QueryPingC2SPacket var1);

    public void onRequest(QueryRequestC2SPacket var1);
}

