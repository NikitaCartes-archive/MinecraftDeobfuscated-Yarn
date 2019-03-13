package net.minecraft.server.network;

import net.minecraft.SharedConstants;
import net.minecraft.client.network.packet.LoginDisconnectS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
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
	public void onHandshake(HandshakeC2SPacket handshakeC2SPacket) {
		switch (handshakeC2SPacket.getIntendedState()) {
			case LOGIN:
				this.client.method_10750(NetworkState.LOGIN);
				if (handshakeC2SPacket.getProtocolVersion() > SharedConstants.getGameVersion().getProtocolVersion()) {
					TextComponent textComponent = new TranslatableTextComponent("multiplayer.disconnect.outdated_server", SharedConstants.getGameVersion().getName());
					this.client.method_10743(new LoginDisconnectS2CPacket(textComponent));
					this.client.method_10747(textComponent);
				} else if (handshakeC2SPacket.getProtocolVersion() < SharedConstants.getGameVersion().getProtocolVersion()) {
					TextComponent textComponent = new TranslatableTextComponent("multiplayer.disconnect.outdated_client", SharedConstants.getGameVersion().getName());
					this.client.method_10743(new LoginDisconnectS2CPacket(textComponent));
					this.client.method_10747(textComponent);
				} else {
					this.client.method_10763(new ServerLoginNetworkHandler(this.server, this.client));
				}
				break;
			case QUERY:
				this.client.method_10750(NetworkState.QUERY);
				this.client.method_10763(new ServerQueryNetworkHandler(this.server, this.client));
				break;
			default:
				throw new UnsupportedOperationException("Invalid intention " + handshakeC2SPacket.getIntendedState());
		}
	}

	@Override
	public void method_10839(TextComponent textComponent) {
	}
}
