package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.message.LastSeenMessageList;

public record MessageAcknowledgmentC2SPacket(LastSeenMessageList.Acknowledgment acknowledgment) implements Packet<ServerPlayPacketListener> {
	public MessageAcknowledgmentC2SPacket(PacketByteBuf buf) {
		this(new LastSeenMessageList.Acknowledgment(buf));
	}

	@Override
	public void write(PacketByteBuf buf) {
		this.acknowledgment.write(buf);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onMessageAcknowledgment(this);
	}
}
