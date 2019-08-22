/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.listener;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.packet.LoginHelloC2SPacket;
import net.minecraft.server.network.packet.LoginKeyC2SPacket;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;

public interface ServerLoginPacketListener
extends PacketListener {
    public void onHello(LoginHelloC2SPacket var1);

    public void onKey(LoginKeyC2SPacket var1);

    public void onQueryResponse(LoginQueryResponseC2SPacket var1);
}

