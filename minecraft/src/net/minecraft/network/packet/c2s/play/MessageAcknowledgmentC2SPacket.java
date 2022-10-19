package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public record MessageAcknowledgmentC2SPacket(int offset) implements Packet<ServerPlayPacketListener> {
	public MessageAcknowledgmentC2SPacket(PacketByteBuf buf) {
		this(buf.readVarInt());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.offset);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onMessageAcknowledgment(this);
	}
}
