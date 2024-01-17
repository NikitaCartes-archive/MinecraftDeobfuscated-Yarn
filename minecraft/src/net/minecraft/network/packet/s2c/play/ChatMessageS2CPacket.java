package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;

/**
 * A packet used to send a chat message to the clients.
 * 
 * <p>The content is not wrapped in any way (e.g. by {@code chat.type.text} text); the
 * raw message content is sent to the clients, and they will wrap it. To register
 * custom wrapping behaviors, check {@link MessageType#register}.
 * 
 * <p>Chat messages have signatures. It is possible to use a bogus signature - such as
 * {@link net.minecraft.network.message.SignedMessage#ofUnsigned} - to send a chat
 * message; however if the signature is invalid (e.g. because the text's content differs
 * from the one sent by the client, or because the passed signature is invalid) the client
 * will show a warning and can discard it depending on the options.
 * 
 * <p>If the message takes more than {@link
 * net.minecraft.network.message.SignedMessage#CLIENTBOUND_TIME_TO_LIVE}
 * to reach the clients (including the time it originally took to reach the server),
 * the message is not considered secure anymore by the clients, and may be discarded
 * depending on the clients' options.
 * 
 * @see net.minecraft.server.network.ServerPlayerEntity#sendChatMessage
 * @see net.minecraft.client.network.ClientPlayNetworkHandler#onChatMessage
 */
public record ChatMessageS2CPacket(
	UUID sender,
	int index,
	@Nullable MessageSignatureData signature,
	MessageBody.Serialized body,
	@Nullable Text unsignedContent,
	FilterMask filterMask,
	MessageType.Serialized serializedParameters
) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ChatMessageS2CPacket> CODEC = Packet.createCodec(ChatMessageS2CPacket::write, ChatMessageS2CPacket::new);

	private ChatMessageS2CPacket(PacketByteBuf buf) {
		this(
			buf.readUuid(),
			buf.readVarInt(),
			buf.readNullable(MessageSignatureData::fromBuf),
			new MessageBody.Serialized(buf),
			buf.readNullable(PacketByteBuf::readUnlimitedText),
			FilterMask.readMask(buf),
			new MessageType.Serialized(buf)
		);
	}

	private void write(PacketByteBuf buf) {
		buf.writeUuid(this.sender);
		buf.writeVarInt(this.index);
		buf.writeNullable(this.signature, MessageSignatureData::write);
		this.body.write(buf);
		buf.writeNullable(this.unsignedContent, PacketByteBuf::writeText);
		FilterMask.writeMask(buf, this.filterMask);
		this.serializedParameters.write(buf);
	}

	@Override
	public PacketIdentifier<ChatMessageS2CPacket> getPacketId() {
		return PlayPackets.PLAYER_CHAT;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChatMessage(this);
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}
}
