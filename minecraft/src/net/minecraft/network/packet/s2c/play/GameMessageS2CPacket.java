package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

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

	public UUID getSender() {
		return this.sender;
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}
}
