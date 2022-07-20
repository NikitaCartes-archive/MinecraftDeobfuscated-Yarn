package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.util.registry.DynamicRegistryManager;

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
 * <p>If the message takes more than {@link SignedMessage#CLIENTBOUND_TIME_TO_LIVE}
 * to reach the clients (including the time it originally took to reach the server),
 * the message is not considered secure anymore by the clients, and may be discarded
 * depending on the clients' options.
 * 
 * @see net.minecraft.server.network.ServerPlayerEntity#sendChatMessage
 * @see net.minecraft.client.network.ClientPlayNetworkHandler#onChatMessage
 */
public record ChatMessageS2CPacket(SignedMessage message, MessageType.Serialized serializedParameters) implements Packet<ClientPlayPacketListener> {
	public ChatMessageS2CPacket(PacketByteBuf buf) {
		this(new SignedMessage(buf), new MessageType.Serialized(buf));
	}

	@Override
	public void write(PacketByteBuf buf) {
		this.message.write(buf);
		this.serializedParameters.write(buf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChatMessage(this);
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}

	public Optional<MessageType.Parameters> getParameters(DynamicRegistryManager dynamicRegistryManager) {
		return this.serializedParameters.toParameters(dynamicRegistryManager);
	}
}
