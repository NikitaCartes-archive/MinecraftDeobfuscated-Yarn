package net.minecraft.network.packet.s2c.play;

import java.util.Objects;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

public record GameMessageS2CPacket(Text content, int typeId) implements Packet<ClientPlayPacketListener> {
	public GameMessageS2CPacket(PacketByteBuf buf) {
		this(buf.readText(), buf.readVarInt());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeText(this.content);
		buf.writeVarInt(this.typeId);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameMessage(this);
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
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
