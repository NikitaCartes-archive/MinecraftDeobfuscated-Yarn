package net.minecraft.network.packet.c2s.handshake;

import net.minecraft.network.ConnectionIntent;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.Packet;

public record HandshakeC2SPacket(int protocolVersion, String address, int port, ConnectionIntent intendedState) implements Packet<ServerHandshakePacketListener> {
	private static final int MAX_ADDRESS_LENGTH = 255;

	@Deprecated
	public HandshakeC2SPacket(int protocolVersion, String address, int port, ConnectionIntent intendedState) {
		this.protocolVersion = protocolVersion;
		this.address = address;
		this.port = port;
		this.intendedState = intendedState;
	}

	public HandshakeC2SPacket(PacketByteBuf buf) {
		this(buf.readVarInt(), buf.readString(255), buf.readUnsignedShort(), ConnectionIntent.byId(buf.readVarInt()));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.protocolVersion);
		buf.writeString(this.address);
		buf.writeShort(this.port);
		buf.writeVarInt(this.intendedState.getId());
	}

	public void apply(ServerHandshakePacketListener serverHandshakePacketListener) {
		serverHandshakePacketListener.onHandshake(this);
	}

	@Override
	public NetworkState getNewNetworkState() {
		return this.intendedState.getState();
	}
}
