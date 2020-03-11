package net.minecraft.network.packet.c2s.handshake;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerHandshakePacketListener;

public class HandshakeC2SPacket implements Packet<ServerHandshakePacketListener> {
	private int protocolVersion;
	private String address;
	private int port;
	private NetworkState intendedState;

	public HandshakeC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public HandshakeC2SPacket(String address, int port, NetworkState intendedState) {
		this.protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
		this.address = address;
		this.port = port;
		this.intendedState = intendedState;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.protocolVersion = buf.readVarInt();
		this.address = buf.readString(255);
		this.port = buf.readUnsignedShort();
		this.intendedState = NetworkState.byId(buf.readVarInt());
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
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
