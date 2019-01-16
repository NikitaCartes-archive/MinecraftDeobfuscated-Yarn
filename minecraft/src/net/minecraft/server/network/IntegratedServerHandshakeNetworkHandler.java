package net.minecraft.server.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.packet.HandshakeServerPacket;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public class IntegratedServerHandshakeNetworkHandler implements ServerHandshakePacketListener {
	private final MinecraftServer server;
	private final ClientConnection client;

	public IntegratedServerHandshakeNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection) {
		this.server = minecraftServer;
		this.client = clientConnection;
	}

	@Override
	public void onHandshake(HandshakeServerPacket handshakeServerPacket) {
		this.client.setState(handshakeServerPacket.getRequestedState());
		this.client.setPacketListener(new ServerLoginNetworkHandler(this.server, this.client));
	}

	@Override
	public void onConnectionLost(TextComponent textComponent) {
	}
}
