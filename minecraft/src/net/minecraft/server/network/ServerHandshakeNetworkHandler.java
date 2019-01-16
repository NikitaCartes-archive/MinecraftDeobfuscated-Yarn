package net.minecraft.server.network;

import net.minecraft.SharedConstants;
import net.minecraft.class_2909;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.packet.HandshakeServerPacket;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class ServerHandshakeNetworkHandler implements ServerHandshakePacketListener {
	private final MinecraftServer server;
	private final ClientConnection client;

	public ServerHandshakeNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection) {
		this.server = minecraftServer;
		this.client = clientConnection;
	}

	@Override
	public void onHandshake(HandshakeServerPacket handshakeServerPacket) {
		switch (handshakeServerPacket.getRequestedState()) {
			case LOGIN:
				this.client.setState(NetworkState.LOGIN);
				if (handshakeServerPacket.getProtocolVersion() > SharedConstants.getGameVersion().getProtocolVersion()) {
					TextComponent textComponent = new TranslatableTextComponent("multiplayer.disconnect.outdated_server", SharedConstants.getGameVersion().getName());
					this.client.sendPacket(new class_2909(textComponent));
					this.client.disconnect(textComponent);
				} else if (handshakeServerPacket.getProtocolVersion() < SharedConstants.getGameVersion().getProtocolVersion()) {
					TextComponent textComponent = new TranslatableTextComponent("multiplayer.disconnect.outdated_client", SharedConstants.getGameVersion().getName());
					this.client.sendPacket(new class_2909(textComponent));
					this.client.disconnect(textComponent);
				} else {
					this.client.setPacketListener(new ServerLoginNetworkHandler(this.server, this.client));
				}
				break;
			case QUERY:
				this.client.setState(NetworkState.QUERY);
				this.client.setPacketListener(new ServerQueryNetworkHandler(this.server, this.client));
				break;
			default:
				throw new UnsupportedOperationException("Invalid intention " + handshakeServerPacket.getRequestedState());
		}
	}

	@Override
	public void onConnectionLost(TextComponent textComponent) {
	}
}
