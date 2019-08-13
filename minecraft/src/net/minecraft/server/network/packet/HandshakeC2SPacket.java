package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.util.PacketByteBuf;

public class HandshakeC2SPacket implements Packet<ServerHandshakePacketListener> {
	private int version;
	private String address;
	private int port;
	private NetworkState state;

	public HandshakeC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public HandshakeC2SPacket(String string, int i, NetworkState networkState) {
		this.version = SharedConstants.getGameVersion().getProtocolVersion();
		this.address = string;
		this.port = i;
		this.state = networkState;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.version = packetByteBuf.readVarInt();
		this.address = packetByteBuf.readString(255);
		this.port = packetByteBuf.readUnsignedShort();
		this.state = NetworkState.byId(packetByteBuf.readVarInt());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.version);
		packetByteBuf.writeString(this.address);
		packetByteBuf.writeShort(this.port);
		packetByteBuf.writeVarInt(this.state.getId());
	}

	public void method_12575(ServerHandshakePacketListener serverHandshakePacketListener) {
		serverHandshakePacketListener.onHandshake(this);
	}

	public NetworkState getIntendedState() {
		return this.state;
	}

	public int getProtocolVersion() {
		return this.version;
	}
}
