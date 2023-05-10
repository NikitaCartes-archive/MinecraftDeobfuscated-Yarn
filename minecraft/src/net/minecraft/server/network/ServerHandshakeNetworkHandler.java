package net.minecraft.server.network;

import net.minecraft.SharedConstants;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;

public class ServerHandshakeNetworkHandler implements ServerHandshakePacketListener {
	private static final Text IGNORING_STATUS_REQUEST_MESSAGE = Text.translatable("disconnect.ignoring_status_request");
	private final MinecraftServer server;
	private final ClientConnection connection;

	public ServerHandshakeNetworkHandler(MinecraftServer server, ClientConnection connection) {
		this.server = server;
		this.connection = connection;
	}

	@Override
	public void onHandshake(HandshakeC2SPacket packet) {
		switch (packet.getIntendedState()) {
			case LOGIN:
				this.connection.setState(NetworkState.LOGIN);
				if (packet.getProtocolVersion() != SharedConstants.getGameVersion().getProtocolVersion()) {
					Text text;
					if (packet.getProtocolVersion() < 754) {
						text = Text.translatable("multiplayer.disconnect.outdated_client", SharedConstants.getGameVersion().getName());
					} else {
						text = Text.translatable("multiplayer.disconnect.incompatible", SharedConstants.getGameVersion().getName());
					}

					this.connection.send(new LoginDisconnectS2CPacket(text));
					this.connection.disconnect(text);
				} else {
					this.connection.setPacketListener(new ServerLoginNetworkHandler(this.server, this.connection));
				}
				break;
			case STATUS:
				ServerMetadata serverMetadata = this.server.getServerMetadata();
				if (this.server.acceptsStatusQuery() && serverMetadata != null) {
					this.connection.setState(NetworkState.STATUS);
					this.connection.setPacketListener(new ServerQueryNetworkHandler(serverMetadata, this.connection));
				} else {
					this.connection.disconnect(IGNORING_STATUS_REQUEST_MESSAGE);
				}
				break;
			default:
				throw new UnsupportedOperationException("Invalid intention " + packet.getIntendedState());
		}
	}

	@Override
	public void onDisconnected(Text reason) {
	}

	@Override
	public boolean isConnectionOpen() {
		return this.connection.isOpen();
	}
}
