package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public record RequestChatPreviewC2SPacket(int queryId, String query) implements Packet<ServerPlayPacketListener> {
	public RequestChatPreviewC2SPacket(PacketByteBuf buf) {
		this(buf.readInt(), buf.readString(256));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.queryId);
		buf.writeString(this.query, 256);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onRequestChatPreview(this);
	}
}
