package net.minecraft.server.network;

import net.minecraft.class_9099;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.c2s.handshake.ConnectionIntent;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

/**
 * A server handshake network handler that exclusively handles local
 * connections.
 * 
 * <p>A local connection is one between a Minecraft client and the
 * Integrated Server it is running.
 * 
 * @see net.minecraft.server.ServerNetworkIo#bindLocal()
 */
public class LocalServerHandshakeNetworkHandler implements ServerHandshakePacketListener {
	private final MinecraftServer server;
	private final ClientConnection connection;

	public LocalServerHandshakeNetworkHandler(MinecraftServer server, ClientConnection connection) {
		this.server = server;
		this.connection = connection;
	}

	@Override
	public void onHandshake(HandshakeC2SPacket packet) {
		if (packet.intendedState() != ConnectionIntent.LOGIN) {
			throw new UnsupportedOperationException("Invalid intention " + packet.intendedState());
		} else {
			this.connection.method_56330(class_9099.field_48247, new ServerLoginNetworkHandler(this.server, this.connection, false));
			this.connection.method_56329(class_9099.field_48248);
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
