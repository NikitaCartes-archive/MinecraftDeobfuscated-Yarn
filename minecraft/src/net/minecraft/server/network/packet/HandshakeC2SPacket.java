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
	public HandshakeC2SPacket(String address, int port, NetworkState networkState) {
		this.version = SharedConstants.getGameVersion().getProtocolVersion();
		this.address = address;
		this.port = port;
		this.state = networkState;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.version = buf.readVarInt();
		this.address = buf.readString(255);
		this.port = buf.readUnsignedShort();
		this.state = NetworkState.byId(buf.readVarInt());
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.version);
		buf.writeString(this.address);
		buf.writeShort(this.port);
		buf.writeVarInt(this.state.getId());
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
