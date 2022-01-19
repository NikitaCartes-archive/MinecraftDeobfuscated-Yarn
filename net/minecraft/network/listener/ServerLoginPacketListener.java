/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.listener;

import net.minecraft.class_6857;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;

public interface ServerLoginPacketListener
extends class_6857 {
    public void onHello(LoginHelloC2SPacket var1);

    public void onKey(LoginKeyC2SPacket var1);

    public void onQueryResponse(LoginQueryResponseC2SPacket var1);
}

