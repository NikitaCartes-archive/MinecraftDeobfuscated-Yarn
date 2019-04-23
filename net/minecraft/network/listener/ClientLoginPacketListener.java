/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.listener;

import net.minecraft.client.network.packet.LoginCompressionS2CPacket;
import net.minecraft.client.network.packet.LoginDisconnectS2CPacket;
import net.minecraft.client.network.packet.LoginHelloS2CPacket;
import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.client.network.packet.LoginSuccessS2CPacket;
import net.minecraft.network.listener.PacketListener;

public interface ClientLoginPacketListener
extends PacketListener {
    public void onHello(LoginHelloS2CPacket var1);

    public void onLoginSuccess(LoginSuccessS2CPacket var1);

    public void onDisconnect(LoginDisconnectS2CPacket var1);

    public void onCompression(LoginCompressionS2CPacket var1);

    public void onQueryRequest(LoginQueryRequestS2CPacket var1);
}

