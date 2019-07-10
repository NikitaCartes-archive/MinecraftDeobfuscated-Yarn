package net.minecraft.server.network;

import net.minecraft.client.network.packet.QueryPongS2CPacket;
import net.minecraft.client.network.packet.QueryResponseS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.packet.QueryPingC2SPacket;
import net.minecraft.server.network.packet.QueryRequestC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ServerQueryNetworkHandler implements ServerQueryPacketListener {
	private static final Text REQUEST_HANDLED = new TranslatableText("multiplayer.status.request_handled");
	private final MinecraftServer server;
	private final ClientConnection client;
	private boolean responseSent;

	public ServerQueryNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection) {
		this.server = minecraftServer;
		this.client = clientConnection;
	}

	@Override
	public void onDisconnected(Text text) {
	}

	@Override
	public ClientConnection getConnection() {
		return this.client;
	}

	@Override
	public void onRequest(QueryRequestC2SPacket queryRequestC2SPacket) {
		if (this.responseSent) {
			this.client.disconnect(REQUEST_HANDLED);
		} else {
			this.responseSent = true;
			this.client.send(new QueryResponseS2CPacket(this.server.getServerMetadata()));
		}
	}

	@Override
	public void onPing(QueryPingC2SPacket queryPingC2SPacket) {
		this.client.send(new QueryPongS2CPacket(queryPingC2SPacket.getStartTime()));
		this.client.disconnect(REQUEST_HANDLED);
	}
}
