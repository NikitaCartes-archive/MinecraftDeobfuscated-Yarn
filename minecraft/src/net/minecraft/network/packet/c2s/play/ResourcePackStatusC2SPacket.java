package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ResourcePackStatusC2SPacket implements Packet<ServerPlayPacketListener> {
	private ResourcePackStatusC2SPacket.Status status;

	public ResourcePackStatusC2SPacket() {
	}

	public ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status status) {
		this.status = status;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.status = buf.readEnumConstant(ResourcePackStatusC2SPacket.Status.class);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.status);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onResourcePackStatus(this);
	}

	public static enum Status {
		SUCCESSFULLY_LOADED,
		DECLINED,
		FAILED_DOWNLOAD,
		ACCEPTED;
	}
}
