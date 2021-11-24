package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

/**
 * A packet used to send a game message to the client.
 * 
 * @see net.minecraft.server.network.ServerPlayerEntity#sendMessage(Text, MessageType, UUID)
 * @see net.minecraft.client.network.ClientPlayNetworkHandler#onGameMessage
 */
public class GameMessageS2CPacket implements Packet<ClientPlayPacketListener> {
	private final Text message;
	private final MessageType type;
	private final UUID sender;

	public GameMessageS2CPacket(Text message, MessageType type, UUID sender) {
		this.message = message;
		this.type = type;
		this.sender = sender;
	}

	public GameMessageS2CPacket(PacketByteBuf buf) {
		this.message = buf.readText();
		this.type = MessageType.byId(buf.readByte());
		this.sender = buf.readUuid();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeText(this.message);
		buf.writeByte(this.type.getId());
		buf.writeUuid(this.sender);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameMessage(this);
	}

	public Text getMessage() {
		return this.message;
	}

	public MessageType getType() {
		return this.type;
	}

	/**
	 * {@return {@linkplain net.minecraft.entity.Entity#getUuid the UUID of the
	 * entity} that sends the message or {@link net.minecraft.util.Util#NIL_UUID}
	 * if the message is not sent by an entity}
	 */
	public UUID getSender() {
		return this.sender;
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}
}
