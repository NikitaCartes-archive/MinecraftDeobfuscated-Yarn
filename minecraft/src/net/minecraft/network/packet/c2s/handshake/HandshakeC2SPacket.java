package net.minecraft.network.packet.c2s.handshake;

import net.minecraft.SharedConstants;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.Packet;

public class HandshakeC2SPacket implements Packet<ServerHandshakePacketListener> {
	private static final int MAX_ADDRESS_LENGTH = 255;
	private final int protocolVersion;
	private final String address;
	private final int port;
	private final NetworkState intendedState;

	public HandshakeC2SPacket(String address, int port, NetworkState intendedState) {
		this.protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
		this.address = address;
		this.port = port;
		this.intendedState = intendedState;
	}

	public HandshakeC2SPacket(PacketByteBuf buf) {
		this.protocolVersion = buf.readVarInt();
		this.address = buf.readString(255);
		this.port = buf.readUnsignedShort();
		this.intendedState = NetworkState.byId(buf.readVarInt());
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

	public NetworkState getIntendedState() {
		return this.intendedState;
	}

	public int getProtocolVersion() {
		return this.protocolVersion;
	}

	public String getAddress() {
		return this.address;
	}

	public int getPort() {
		return this.port;
	}
}
