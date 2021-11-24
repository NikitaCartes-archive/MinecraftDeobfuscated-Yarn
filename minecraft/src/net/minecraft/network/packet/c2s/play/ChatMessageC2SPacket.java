package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

/**
 * A packet used to send a chat message to the server.
 * 
 * <p>This truncates the message to at most {@value #MAX_LENGTH} characters
 * before sending to the server on the client. If the server receives the
 * message longer than {@value #MAX_LENGTH} characters, it will reject
 * the message and disconnect the client.
 * 
 * <p>If the message contains an invalid character (see {@link
 * net.minecraft.SharedConstants#isValidChar isValidChar}), the server will
 * reject the message and disconnect the client.
 * 
 * @see net.minecraft.client.network.ClientPlayerEntity#sendChatMessage
 * @see net.minecraft.server.network.ServerPlayNetworkHandler#onChatMessage
 */
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
		serverPlayPacketListener.onChatMessage(this);
	}

	public String getChatMessage() {
		return this.chatMessage;
	}
}
