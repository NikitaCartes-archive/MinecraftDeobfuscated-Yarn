/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.packet.HandshakeC2SPacket;

@Environment(value=EnvType.CLIENT)
public class IntegratedServerHandshakeNetworkHandler
implements ServerHandshakePacketListener {
    private final MinecraftServer server;
    private final ClientConnection client;

    public IntegratedServerHandshakeNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection) {
        this.server = minecraftServer;
        this.client = clientConnection;
    }

    @Override
    public void onHandshake(HandshakeC2SPacket handshakeC2SPacket) {
        this.client.setState(handshakeC2SPacket.getIntendedState());
        this.client.setPacketListener(new ServerLoginNetworkHandler(this.server, this.client));
    }

    @Override
    public void onDisconnected(Component component) {
    }
}

