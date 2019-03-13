package net.minecraft.server.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
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
	public void onHandshake(HandshakeC2SPacket handshakeC2SPacket) {
		this.client.method_10750(handshakeC2SPacket.getIntendedState());
		this.client.method_10763(new ServerLoginNetworkHandler(this.server, this.client));
	}

	@Override
	public void method_10839(TextComponent textComponent) {
	}
}
