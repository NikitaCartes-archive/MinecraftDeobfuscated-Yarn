package net.minecraft.network.packet.c2s.common;

import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerCommonPacketListener;
import net.minecraft.network.packet.Packet;

public record ResourcePackStatusC2SPacket(UUID id, ResourcePackStatusC2SPacket.Status status) implements Packet<ServerCommonPacketListener> {
	public ResourcePackStatusC2SPacket(PacketByteBuf buf) {
		this(buf.readUuid(), buf.readEnumConstant(ResourcePackStatusC2SPacket.Status.class));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeUuid(this.id);
		buf.writeEnumConstant(this.status);
	}

	public void apply(ServerCommonPacketListener serverCommonPacketListener) {
		serverCommonPacketListener.onResourcePackStatus(this);
	}

	public static enum Status {
		SUCCESSFULLY_LOADED,
		DECLINED,
		FAILED_DOWNLOAD,
		ACCEPTED,
		DOWNLOADED,
		INVALID_URL,
		FAILED_RELOAD,
		DISCARDED;

		public boolean hasFinished() {
			return this != ACCEPTED && this != DOWNLOADED;
		}
	}
}
