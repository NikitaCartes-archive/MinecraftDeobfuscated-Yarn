package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ChatMessageC2SPacket implements Packet<ServerPlayPacketListener> {
	private String chatMessage;

	public ChatMessageC2SPacket() {
	}

	public ChatMessageC2SPacket(String chatMessage) {
		if (chatMessage.length() > 256) {
			chatMessage = chatMessage.substring(0, 256);
		}

		this.chatMessage = chatMessage;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.chatMessage = buf.readString(256);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.chatMessage);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onChatMessage(this);
	}

	public String getChatMessage() {
		return this.chatMessage;
	}
}
