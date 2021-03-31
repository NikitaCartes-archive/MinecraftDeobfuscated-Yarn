package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class ChatMessageC2SPacket implements Packet<ServerPlayPacketListener> {
	private static final int MAX_LENGTH = 256;
	private final String chatMessage;

	public ChatMessageC2SPacket(String chatMessage) {
		if (chatMessage.length() > 256) {
			chatMessage = chatMessage.substring(0, 256);
		}

		this.chatMessage = chatMessage;
	}

	public ChatMessageC2SPacket(PacketByteBuf buf) {
		this.chatMessage = buf.readString(256);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.chatMessage);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onGameMessage(this);
	}

	public String getChatMessage() {
		return this.chatMessage;
	}
}
