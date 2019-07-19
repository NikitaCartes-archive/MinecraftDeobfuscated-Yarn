/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class IntegratedServerHandshakeNetworkHandler
implements ServerHandshakePacketListener {
    private final MinecraftServer server;
    private final ClientConnection connection;

    public IntegratedServerHandshakeNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection) {
        this.server = minecraftServer;
        this.connection = clientConnection;
    }

    @Override
    public void onHandshake(HandshakeC2SPacket handshakeC2SPacket) {
        this.connection.setState(handshakeC2SPacket.getIntendedState());
        this.connection.setPacketListener(new ServerLoginNetworkHandler(this.server, this.connection));
    }

    @Override
    public void onDisconnected(Text text) {
    }

    @Override
    public ClientConnection getConnection() {
        return this.connection;
    }
}

