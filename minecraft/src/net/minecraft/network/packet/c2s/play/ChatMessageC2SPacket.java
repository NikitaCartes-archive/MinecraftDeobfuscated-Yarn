package net.minecraft.network.packet.c2s.play;

import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageSignatureData;

/**
 * A packet used to send a chat message to the server.
 * 
 * <p>This truncates the message to at most 256 characters before sending to
 * the server on the client. If the server receives the message longer than
 * 256 characters, it will reject the message and disconnect the client.
 * 
 * <p>If the message contains an invalid character (see {@link
 * net.minecraft.SharedConstants#isValidChar}) or if the server
 * receives the messages in improper order. the server will
 * reject the message and disconnect the client.
 * 
 * <p>Messages that took more than {@link
 * net.minecraft.network.message.SignedMessage#SERVERBOUND_TIME_TO_LIVE}
 * to reach the server are considered expired and log warnings on the server.
 * If the message takes more than {@link
 * net.minecraft.network.message.SignedMessage#CLIENTBOUND_TIME_TO_LIVE}
 * to reach the clients (including the time it took to reach the server), the
 * message is not considered secure anymore by the clients, and may be discarded
 * depending on the clients' options.
 * 
 * @see net.minecraft.client.network.ClientPlayNetworkHandler#sendChatMessage
 * @see net.minecraft.server.network.ServerPlayNetworkHandler#onChatMessage
 */
public record ChatMessageC2SPacket(
	String chatMessage, Instant timestamp, long salt, @Nullable MessageSignatureData signature, LastSeenMessageList.Acknowledgment acknowledgment
) implements Packet<ServerPlayPacketListener> {
	public ChatMessageC2SPacket(PacketByteBuf buf) {
		this(buf.readString(256), buf.readInstant(), buf.readLong(), buf.readNullable(MessageSignatureData::fromBuf), new LastSeenMessageList.Acknowledgment(buf));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.chatMessage, 256);
		buf.writeInstant(this.timestamp);
		buf.writeLong(this.salt);
		buf.writeNullable(this.signature, MessageSignatureData::write);
		this.acknowledgment.write(buf);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onChatMessage(this);
	}
}
