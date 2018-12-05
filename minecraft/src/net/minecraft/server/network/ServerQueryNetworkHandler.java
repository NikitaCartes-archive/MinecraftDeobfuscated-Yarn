package net.minecraft.server.network;

import net.minecraft.client.network.packet.QueryPongClientPacket;
import net.minecraft.client.network.packet.QueryResponseClientPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.packet.QueryPingServerPacket;
import net.minecraft.server.network.packet.QueryRequestServerPacket;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class ServerQueryNetworkHandler implements ServerQueryPacketListener {
	private static final TextComponent REQUEST_HANDLED = new TranslatableTextComponent("multiplayer.status.request_handled");
	private final MinecraftServer server;
	private final ClientConnection client;
	private boolean field_14177;

	public ServerQueryNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection) {
		this.server = minecraftServer;
		this.client = clientConnection;
	}

	@Override
	public void onConnectionLost(TextComponent textComponent) {
	}

	@Override
	public void onRequest(QueryRequestServerPacket queryRequestServerPacket) {
		if (this.field_14177) {
			this.client.disconnect(REQUEST_HANDLED);
		} else {
			this.field_14177 = true;
			this.client.sendPacket(new QueryResponseClientPacket(this.server.getServerMetadata()));
		}
	}

	@Override
	public void onPing(QueryPingServerPacket queryPingServerPacket) {
		this.client.sendPacket(new QueryPongClientPacket(queryPingServerPacket.getStartTime()));
		this.client.disconnect(REQUEST_HANDLED);
	}
}
