package net.minecraft.server.network;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ServerQueryNetworkHandler implements ServerQueryPacketListener {
	private static final Text REQUEST_HANDLED = new TranslatableText("multiplayer.status.request_handled");
	private final MinecraftServer server;
	private final ClientConnection connection;
	private boolean responseSent;

	public ServerQueryNetworkHandler(MinecraftServer server, ClientConnection connection) {
		this.server = server;
		this.connection = connection;
	}

	@Override
	public void onDisconnected(Text reason) {
	}

	@Override
	public ClientConnection getConnection() {
		return this.connection;
	}

	@Override
	public void onRequest(QueryRequestC2SPacket packet) {
		if (this.responseSent) {
			this.connection.disconnect(REQUEST_HANDLED);
		} else {
			this.responseSent = true;
			this.connection.send(new QueryResponseS2CPacket(this.server.getServerMetadata()));
		}
	}

	@Override
	public void onPing(QueryPingC2SPacket packet) {
		this.connection.send(new QueryPongS2CPacket(packet.getStartTime()));
		this.connection.disconnect(REQUEST_HANDLED);
	}
}
