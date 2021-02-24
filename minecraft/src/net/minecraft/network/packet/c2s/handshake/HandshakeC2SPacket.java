package net.minecraft.network.packet.c2s.handshake;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerHandshakePacketListener;

public class HandshakeC2SPacket implements Packet<ServerHandshakePacketListener> {
	private final int protocolVersion;
	private final String address;
	private final int port;
	private final NetworkState intendedState;

	@Environment(EnvType.CLIENT)
	public HandshakeC2SPacket(String address, int port, NetworkState intendedState) {
		this.protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
		this.address = address;
		this.port = port;
		this.intendedState = intendedState;
	}

	public HandshakeC2SPacket(PacketByteBuf packetByteBuf) {
		this.protocolVersion = packetByteBuf.readVarInt();
		this.address = packetByteBuf.readString(255);
		this.port = packetByteBuf.readUnsignedShort();
		this.intendedState = NetworkState.byId(packetByteBuf.readVarInt());
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
}
