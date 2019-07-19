package net.minecraft.server.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class IntegratedServerHandshakeNetworkHandler implements ServerHandshakePacketListener {
	private final MinecraftServer server;
	private final ClientConnection connection;

	public IntegratedServerHandshakeNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection) {
		this.server = minecraftServer;
		this.connection = clientConnection;
	}

	@Override
	public void onHandshake(HandshakeC2SPacket packet) {
		this.connection.setState(packet.getIntendedState());
		this.connection.setPacketListener(new ServerLoginNetworkHandler(this.server, this.connection));
	}

	@Override
	public void onDisconnected(Text reason) {
	}

	@Override
	public ClientConnection getConnection() {
		return this.connection;
	}
}
