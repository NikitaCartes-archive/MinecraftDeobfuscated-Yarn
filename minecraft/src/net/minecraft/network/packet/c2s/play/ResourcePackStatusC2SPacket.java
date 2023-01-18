package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class ResourcePackStatusC2SPacket implements Packet<ServerPlayPacketListener> {
	private final ResourcePackStatusC2SPacket.Status status;

	public ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status status) {
		this.status = status;
	}

	public ResourcePackStatusC2SPacket(PacketByteBuf buf) {
		this.status = buf.readEnumConstant(ResourcePackStatusC2SPacket.Status.class);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeEnumConstant(this.status);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onResourcePackStatus(this);
	}

	public ResourcePackStatusC2SPacket.Status getStatus() {
		return this.status;
	}

	public static enum Status {
		SUCCESSFULLY_LOADED,
		DECLINED,
		FAILED_DOWNLOAD,
		ACCEPTED;
	}
}
