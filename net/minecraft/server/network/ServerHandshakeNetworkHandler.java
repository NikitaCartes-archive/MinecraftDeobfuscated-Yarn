/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.network;

import net.minecraft.SharedConstants;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerQueryNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ServerHandshakeNetworkHandler
implements ServerHandshakePacketListener {
    private final MinecraftServer server;
    private final ClientConnection connection;

    public ServerHandshakeNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection) {
        this.server = minecraftServer;
        this.connection = clientConnection;
    }

    @Override
    public void onHandshake(HandshakeC2SPacket packet) {
        switch (packet.getIntendedState()) {
            case LOGIN: {
                this.connection.setState(NetworkState.LOGIN);
                if (packet.getProtocolVersion() > SharedConstants.getGameVersion().getProtocolVersion()) {
                    TranslatableText text = new TranslatableText("multiplayer.disconnect.outdated_server", SharedConstants.getGameVersion().getName());
                    this.connection.send(new LoginDisconnectS2CPacket(text));
                    this.connection.disconnect(text);
                    break;
                }
                if (packet.getProtocolVersion() < SharedConstants.getGameVersion().getProtocolVersion()) {
                    TranslatableText text = new TranslatableText("multiplayer.disconnect.outdated_client", SharedConstants.getGameVersion().getName());
                    this.connection.send(new LoginDisconnectS2CPacket(text));
                    this.connection.disconnect(text);
                    break;
                }
                this.connection.setPacketListener(new ServerLoginNetworkHandler(this.server, this.connection));
                break;
            }
            case STATUS: {
                this.connection.setState(NetworkState.STATUS);
                this.connection.setPacketListener(new ServerQueryNetworkHandler(this.server, this.connection));
                break;
            }
            default: {
                throw new UnsupportedOperationException("Invalid intention " + (Object)((Object)packet.getIntendedState()));
            }
        }
    }

    @Override
    public void onDisconnected(Text reason) {
    }

    @Override
    public ClientConnection getConnection() {
        return this.connection;
    }
}

