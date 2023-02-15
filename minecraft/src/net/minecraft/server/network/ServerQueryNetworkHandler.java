package net.minecraft.server.network;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;

public class ServerQueryNetworkHandler implements ServerQueryPacketListener {
	private static final Text REQUEST_HANDLED = Text.translatable("multiplayer.status.request_handled");
	private final ServerMetadata metadata;
	private final ClientConnection connection;
	private boolean responseSent;

	public ServerQueryNetworkHandler(ServerMetadata metadata, ClientConnection connection) {
		this.metadata = metadata;
		this.connection = connection;
	}

	@Override
	public void onDisconnected(Text reason) {
	}

	@Override
	public boolean isConnectionOpen() {
		return this.connection.isOpen();
	}

	@Override
	public void onRequest(QueryRequestC2SPacket packet) {
		if (this.responseSent) {
			this.connection.disconnect(REQUEST_HANDLED);
		} else {
			this.responseSent = true;
			this.connection.send(new QueryResponseS2CPacket(this.metadata));
		}
	}

	@Override
	public void onPing(QueryPingC2SPacket packet) {
		this.connection.send(new QueryPongS2CPacket(packet.getStartTime()));
		this.connection.disconnect(REQUEST_HANDLED);
	}
}
