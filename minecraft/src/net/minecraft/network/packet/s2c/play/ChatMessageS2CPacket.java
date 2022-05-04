package net.minecraft.network.packet.s2c.play;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import net.minecraft.network.MessageSender;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.ChatMessageSignature;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.SignedChatMessage;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

/**
 * A packet used to send a chat message to the clients.
 * 
 * <p>The content is not wrapped in any way (e.g. by {@code chat.type.text} text); the
 * raw message content is sent to the clients, and they will wrap it. To register
 * custom wrapping behaviors, check {@link MessageType#register}.
 * 
 * <p>Messages that took more than {@link #TIME_TO_LIVE} to reach the clients are
 * considered expired. This is measured from the time the client sent the chat message
 * to the server. Note that unlike {@link
 * net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket} expired messages are not
 * discarded by the clients; they instead log a warning.
 * 
 * <p>Chat messages have signatures. It is possible to use a bogus signature - such as
 * {@link net.minecraft.network.encryption.ChatMessageSignature#none} - to send a chat
 * message; however if the signature is invalid (e.g. because the text's content differs
 * from the one sent by the client, or because the passed signature is invalid) the client
 * will log a warning. See {@link
 * net.minecraft.network.encryption.ChatMessageSignature#updateSignature} for how the
 * message is signed.
 * 
 * @see net.minecraft.server.network.ServerPlayerEntity#sendChatMessage
 * @see net.minecraft.client.network.ClientPlayNetworkHandler#onChatMessage
 */
public record ChatMessageS2CPacket(Text content, int typeId, MessageSender sender, Instant time, NetworkEncryptionUtils.SignatureData saltSignature)
	implements Packet<ClientPlayPacketListener> {
	private static final Duration TIME_TO_LIVE = ChatMessageC2SPacket.TIME_TO_LIVE.plus(Duration.ofMinutes(2L));

	public ChatMessageS2CPacket(PacketByteBuf buf) {
		this(buf.readText(), buf.readVarInt(), new MessageSender(buf), Instant.ofEpochSecond(buf.readLong()), new NetworkEncryptionUtils.SignatureData(buf));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeText(this.content);
		buf.writeVarInt(this.typeId);
		this.sender.write(buf);
		buf.writeLong(this.time.getEpochSecond());
		this.saltSignature.write(buf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChatMessage(this);
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}

	public SignedChatMessage getSignedMessage() {
		ChatMessageSignature chatMessageSignature = new ChatMessageSignature(this.sender.uuid(), this.time, this.saltSignature);
		return new SignedChatMessage(this.content, chatMessageSignature);
	}

	/**
	 * {@return when the message is considered expired}
	 */
	private Instant getExpiryTime() {
		return this.time.plus(TIME_TO_LIVE);
	}

	/**
	 * {@return whether the message is considered expired}
	 */
	public boolean isExpired(Instant currentTime) {
		return currentTime.isAfter(this.getExpiryTime());
	}

	/**
	 * {@return the message type of the chat message}
	 * 
	 * @throws NullPointerException when the type ID is invalid (due to unsynced registry, etc)
	 */
	public MessageType getMessageType(Registry<MessageType> registry) {
		return (MessageType)Objects.requireNonNull(registry.get(this.typeId), "Invalid chat type");
	}
}
