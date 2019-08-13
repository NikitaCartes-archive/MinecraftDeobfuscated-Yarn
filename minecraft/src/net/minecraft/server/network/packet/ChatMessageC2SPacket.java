package net.minecraft.server.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ChatMessageC2SPacket implements Packet<ServerPlayPacketListener> {
	private String chatMessage;

	public ChatMessageC2SPacket() {
	}

	public ChatMessageC2SPacket(String string) {
		if (string.length() > 256) {
			string = string.substring(0, 256);
		}

		this.chatMessage = string;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.chatMessage = packetByteBuf.readString(256);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.chatMessage);
	}

	public void method_12115(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onChatMessage(this);
	}

	public String getChatMessage() {
		return this.chatMessage;
	}
}
