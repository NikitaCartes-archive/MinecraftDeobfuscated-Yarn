package net.minecraft.server.network;

import net.minecraft.SharedConstants;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.state.LoginStates;
import net.minecraft.network.state.QueryStates;
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
		switch (packet.intendedState()) {
			case LOGIN:
				this.login(packet, false);
				break;
			case STATUS:
				ServerMetadata serverMetadata = this.server.getServerMetadata();
				this.connection.transitionOutbound(QueryStates.S2C);
				if (this.server.acceptsStatusQuery() && serverMetadata != null) {
					this.connection.transitionInbound(QueryStates.C2S, new ServerQueryNetworkHandler(serverMetadata, this.connection));
				} else {
					this.connection.disconnect(IGNORING_STATUS_REQUEST_MESSAGE);
				}
				break;
			case TRANSFER:
				if (!this.server.acceptsTransfers()) {
					this.connection.transitionOutbound(LoginStates.S2C);
					Text text = Text.translatable("multiplayer.disconnect.transfers_disabled");
					this.connection.send(new LoginDisconnectS2CPacket(text));
					this.connection.disconnect(text);
				} else {
					this.login(packet, true);
				}
				break;
			default:
				throw new UnsupportedOperationException("Invalid intention " + packet.intendedState());
		}
	}

	private void login(HandshakeC2SPacket packet, boolean transfer) {
		this.connection.transitionOutbound(LoginStates.S2C);
		if (packet.protocolVersion() != SharedConstants.getGameVersion().getProtocolVersion()) {
			Text text;
			if (packet.protocolVersion() < 754) {
				text = Text.translatable("multiplayer.disconnect.outdated_client", SharedConstants.getGameVersion().getName());
			} else {
				text = Text.translatable("multiplayer.disconnect.incompatible", SharedConstants.getGameVersion().getName());
			}

			this.connection.send(new LoginDisconnectS2CPacket(text));
			this.connection.disconnect(text);
		} else {
			this.connection.transitionInbound(LoginStates.C2S, new ServerLoginNetworkHandler(this.server, this.connection, transfer));
		}
	}

	@Override
	public void onDisconnected(DisconnectionInfo info) {
	}

	@Override
	public boolean isConnectionOpen() {
		return this.connection.isOpen();
	}
}
