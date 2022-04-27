package net.minecraft.network.packet.c2s.play;

import java.time.Duration;
import java.time.Instant;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.ServerPlayPacketListener;
import org.apache.commons.lang3.StringUtils;

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
 * <p>Messages that took more than {@link #TIME_TO_LIVE} to reach the server
 * are considered expired and will be discarded.
 * 
 * @see net.minecraft.client.network.ClientPlayerEntity#sendChatMessage
 * @see net.minecraft.server.network.ServerPlayNetworkHandler#onChatMessage
 */
public class ChatMessageC2SPacket implements Packet<ServerPlayPacketListener> {
	private static final int MAX_LENGTH = 256;
	public static final Duration TIME_TO_LIVE = Duration.ofMinutes(2L);
	private final Instant time;
	private final String chatMessage;
	private final NetworkEncryptionUtils.SignatureData signature;

	public ChatMessageC2SPacket(Instant time, String chatMessage, NetworkEncryptionUtils.SignatureData signature) {
		this.time = time;
		this.chatMessage = truncateMessage(chatMessage);
		this.signature = signature;
	}

	public ChatMessageC2SPacket(PacketByteBuf buf) {
		this.time = Instant.ofEpochSecond(buf.readLong());
		this.chatMessage = buf.readString(256);
		this.signature = new NetworkEncryptionUtils.SignatureData(buf);
	}

	/**
	 * {@return the message truncated to at most {@value #MAX_LENGTH} characters}
	 */
	private static String truncateMessage(String message) {
		return message.length() > 256 ? message.substring(0, 256) : message;
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeLong(this.time.getEpochSecond());
		buf.writeString(this.chatMessage);
		this.signature.write(buf);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onChatMessage(this);
	}

	/**
	 * {@return when the client sent the message to the server}
	 */
	public Instant getTime() {
		return this.time;
	}

	public String getChatMessage() {
		return this.chatMessage;
	}

	/**
	 * {@return the chat message with spaces normalized}
	 */
	public String getNormalizedChatMessage() {
		return StringUtils.normalizeSpace(this.chatMessage);
	}

	public NetworkEncryptionUtils.SignatureData getSignature() {
		return this.signature;
	}

	/**
	 * {@return when the message is considered expired and should be discarded}
	 */
	private Instant getExpiryTime() {
		return this.time.plus(TIME_TO_LIVE);
	}

	/**
	 * {@return whether the message is considered expired and should be discarded}
	 */
	public boolean isExpired(Instant currentTime) {
		return currentTime.isAfter(this.getExpiryTime());
	}
}
