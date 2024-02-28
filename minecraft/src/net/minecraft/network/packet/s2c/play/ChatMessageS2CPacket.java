package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

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
	MessageType.Parameters serializedParameters
) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, ChatMessageS2CPacket> CODEC = Packet.createCodec(ChatMessageS2CPacket::write, ChatMessageS2CPacket::new);

	private ChatMessageS2CPacket(RegistryByteBuf buf) {
		this(
			buf.readUuid(),
			buf.readVarInt(),
			buf.readNullable(MessageSignatureData::fromBuf),
			new MessageBody.Serialized(buf),
			PacketByteBuf.readNullable(buf, TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC),
			FilterMask.readMask(buf),
			MessageType.Parameters.CODEC.decode(buf)
		);
	}

	private void write(RegistryByteBuf buf) {
		buf.writeUuid(this.sender);
		buf.writeVarInt(this.index);
		buf.writeNullable(this.signature, MessageSignatureData::write);
		this.body.write(buf);
		PacketByteBuf.writeNullable(buf, this.unsignedContent, TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC);
		FilterMask.writeMask(buf, this.filterMask);
		MessageType.Parameters.CODEC.encode(buf, this.serializedParameters);
	}

	@Override
	public PacketType<ChatMessageS2CPacket> getPacketId() {
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
