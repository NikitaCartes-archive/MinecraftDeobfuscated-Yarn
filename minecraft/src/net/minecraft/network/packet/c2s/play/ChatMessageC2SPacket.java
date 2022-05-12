package net.minecraft.network.packet.c2s.play;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.ChatMessageSignature;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.StringHelper;

/**
 * A packet used to send a chat message to the server.
 * 
 * <p>This truncates the message to at most 256 characters before sending to
 * the server on the client. If the server receives the message longer than
 * 256 characters, it will reject the message and disconnect the client.
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
	public static final Duration TIME_TO_LIVE = Duration.ofMinutes(2L);
	private final String chatMessage;
	private final Instant time;
	private final NetworkEncryptionUtils.SignatureData signature;
	private final boolean previewed;

	public ChatMessageC2SPacket(String chatMessage, ChatMessageSignature signature, boolean previewed) {
		this.chatMessage = StringHelper.truncateChat(chatMessage);
		this.time = signature.timestamp();
		this.signature = signature.saltSignature();
		this.previewed = previewed;
	}

	public ChatMessageC2SPacket(PacketByteBuf buf) {
		this.chatMessage = buf.readString(256);
		this.time = buf.readInstant();
		this.signature = new NetworkEncryptionUtils.SignatureData(buf);
		this.previewed = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.chatMessage);
		buf.writeInstant(this.time);
		NetworkEncryptionUtils.SignatureData.write(buf, this.signature);
		buf.writeBoolean(this.previewed);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onChatMessage(this);
	}

	public String getChatMessage() {
		return this.chatMessage;
	}

	public ChatMessageSignature createSignatureInstance(UUID sender) {
		return new ChatMessageSignature(sender, this.time, this.signature);
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

	/**
	 * {@return whether the chat message was previewed before sending}
	 * 
	 * @apiNote Chat decorators can produce signed decorated content only if it was previewed.
	 */
	public boolean isPreviewed() {
		return this.previewed;
	}
}
