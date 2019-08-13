package net.minecraft.server.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ClientStatusC2SPacket implements Packet<ServerPlayPacketListener> {
	private ClientStatusC2SPacket.Mode mode;

	public ClientStatusC2SPacket() {
	}

	public ClientStatusC2SPacket(ClientStatusC2SPacket.Mode mode) {
		this.mode = mode;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.mode = packetByteBuf.readEnumConstant(ClientStatusC2SPacket.Mode.class);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.mode);
	}

	public void method_12120(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClientStatus(this);
	}

	public ClientStatusC2SPacket.Mode getMode() {
		return this.mode;
	}

	public static enum Mode {
		field_12774,
		field_12775;
	}
}
