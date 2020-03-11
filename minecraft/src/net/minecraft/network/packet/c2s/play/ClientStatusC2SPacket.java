package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class ClientStatusC2SPacket implements Packet<ServerPlayPacketListener> {
	private ClientStatusC2SPacket.Mode mode;

	public ClientStatusC2SPacket() {
	}

	public ClientStatusC2SPacket(ClientStatusC2SPacket.Mode mode) {
		this.mode = mode;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.mode = buf.readEnumConstant(ClientStatusC2SPacket.Mode.class);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.mode);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClientStatus(this);
	}

	public ClientStatusC2SPacket.Mode getMode() {
		return this.mode;
	}

	public static enum Mode {
		PERFORM_RESPAWN,
		REQUEST_STATS;
	}
}
