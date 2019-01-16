package net.minecraft.server.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ClientStatusServerPacket implements Packet<ServerPlayPacketListener> {
	private ClientStatusServerPacket.Mode mode;

	public ClientStatusServerPacket() {
	}

	public ClientStatusServerPacket(ClientStatusServerPacket.Mode mode) {
		this.mode = mode;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.mode = packetByteBuf.readEnumConstant(ClientStatusServerPacket.Mode.class);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.mode);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClientStatus(this);
	}

	public ClientStatusServerPacket.Mode getMode() {
		return this.mode;
	}

	public static enum Mode {
		field_12774,
		field_12775;
	}
}
