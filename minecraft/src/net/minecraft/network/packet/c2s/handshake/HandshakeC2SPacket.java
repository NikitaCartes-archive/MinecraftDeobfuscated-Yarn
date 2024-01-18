package net.minecraft.network.packet.c2s.handshake;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.HandshakePackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public record HandshakeC2SPacket(int protocolVersion, String address, int port, ConnectionIntent intendedState) implements Packet<ServerHandshakePacketListener> {
	public static final PacketCodec<PacketByteBuf, HandshakeC2SPacket> CODEC = Packet.createCodec(HandshakeC2SPacket::write, HandshakeC2SPacket::new);
	private static final int MAX_ADDRESS_LENGTH = 255;

	@Deprecated
	public HandshakeC2SPacket(int protocolVersion, String address, int port, ConnectionIntent intendedState) {
		this.protocolVersion = protocolVersion;
		this.address = address;
		this.port = port;
		this.intendedState = intendedState;
	}

	private HandshakeC2SPacket(PacketByteBuf buf) {
		this(buf.readVarInt(), buf.readString(255), buf.readUnsignedShort(), ConnectionIntent.byId(buf.readVarInt()));
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.protocolVersion);
		buf.writeString(this.address);
		buf.writeShort(this.port);
		buf.writeVarInt(this.intendedState.getId());
	}

	@Override
	public PacketType<HandshakeC2SPacket> getPacketId() {
		return HandshakePackets.INTENTION;
	}

	public void apply(ServerHandshakePacketListener serverHandshakePacketListener) {
		serverHandshakePacketListener.onHandshake(this);
	}

	@Override
	public boolean transitionsNetworkState() {
		return true;
	}
}
