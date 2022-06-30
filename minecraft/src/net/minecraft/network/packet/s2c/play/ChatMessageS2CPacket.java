package net.minecraft.network.packet.s2c.play;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageSignature;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

/**
 * A packet used to send a chat message to the clients.
 * 
 * <p>The content is not wrapped in any way (e.g. by {@code chat.type.text} text); the
 * raw message content is sent to the clients, and they will wrap it. To register
 * custom wrapping behaviors, check {@link MessageType#register}.
 * 
 * <p>Chat messages have signatures. It is possible to use a bogus signature - such as
 * {@link net.minecraft.network.message.MessageSignature#none} - to send a chat
 * message; however if the signature is invalid (e.g. because the text's content differs
 * from the one sent by the client, or because the passed signature is invalid) the client
 * will show a warning and can discard it depending on the options. See {@link
 * net.minecraft.network.message.MessageSignature#updateSignature} for how the
 * message is signed.
 * 
 * <p>If the message takes more than {@link SignedMessage#CLIENTBOUND_TIME_TO_LIVE}
 * to reach the clients (including the time it originally took to reach the server),
 * the message is not considered secure anymore by the clients, and may be discarded
 * depending on the clients' options.
 * 
 * @see net.minecraft.server.network.ServerPlayerEntity#sendChatMessage
 * @see net.minecraft.client.network.ClientPlayNetworkHandler#onChatMessage
 */
public record ChatMessageS2CPacket(
	Text signedContent, Optional<Text> unsignedContent, int typeId, MessageSender sender, Instant timestamp, NetworkEncryptionUtils.SignatureData saltSignature
) implements Packet<ClientPlayPacketListener> {
	public ChatMessageS2CPacket(PacketByteBuf buf) {
		this(
			buf.readText(),
			buf.readOptional(PacketByteBuf::readText),
			buf.readVarInt(),
			new MessageSender(buf),
			buf.readInstant(),
			new NetworkEncryptionUtils.SignatureData(buf)
		);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeText(this.signedContent);
		buf.writeOptional(this.unsignedContent, PacketByteBuf::writeText);
		buf.writeVarInt(this.typeId);
		this.sender.write(buf);
		buf.writeInstant(this.timestamp);
		NetworkEncryptionUtils.SignatureData.write(buf, this.saltSignature);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChatMessage(this);
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}

	public SignedMessage getSignedMessage() {
		MessageSignature messageSignature = new MessageSignature(this.sender.profileId(), this.timestamp, this.saltSignature);
		return new SignedMessage(this.signedContent, messageSignature, this.unsignedContent);
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
