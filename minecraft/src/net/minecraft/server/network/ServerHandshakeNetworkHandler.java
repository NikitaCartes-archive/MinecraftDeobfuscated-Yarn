package net.minecraft.server.network;

import net.minecraft.SharedConstants;
import net.minecraft.class_9099;
import net.minecraft.class_9103;
import net.minecraft.network.ClientConnection;
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
		switch (packet.intendedState()) {
			case LOGIN:
				this.method_56048(packet, false);
				break;
			case STATUS:
				ServerMetadata serverMetadata = this.server.getServerMetadata();
				this.connection.method_56329(class_9103.field_48264);
				if (this.server.acceptsStatusQuery() && serverMetadata != null) {
					this.connection.method_56330(class_9103.field_48263, new ServerQueryNetworkHandler(serverMetadata, this.connection));
				} else {
					this.connection.disconnect(IGNORING_STATUS_REQUEST_MESSAGE);
				}
				break;
			case TRANSFER:
				if (!this.server.method_56040()) {
					this.connection.method_56329(class_9099.field_48248);
					Text text = Text.translatable("multiplayer.disconnect.transfers_disabled");
					this.connection.send(new LoginDisconnectS2CPacket(text));
					this.connection.disconnect(text);
				} else {
					this.method_56048(packet, true);
				}
				break;
			default:
				throw new UnsupportedOperationException("Invalid intention " + packet.intendedState());
		}
	}

	private void method_56048(HandshakeC2SPacket handshakeC2SPacket, boolean bl) {
		this.connection.method_56329(class_9099.field_48248);
		if (handshakeC2SPacket.protocolVersion() != SharedConstants.getGameVersion().getProtocolVersion()) {
			Text text;
			if (handshakeC2SPacket.protocolVersion() < 754) {
				text = Text.translatable("multiplayer.disconnect.outdated_client", SharedConstants.getGameVersion().getName());
			} else {
				text = Text.translatable("multiplayer.disconnect.incompatible", SharedConstants.getGameVersion().getName());
			}

			this.connection.send(new LoginDisconnectS2CPacket(text));
			this.connection.disconnect(text);
		} else {
			this.connection.method_56330(class_9099.field_48247, new ServerLoginNetworkHandler(this.server, this.connection, bl));
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
