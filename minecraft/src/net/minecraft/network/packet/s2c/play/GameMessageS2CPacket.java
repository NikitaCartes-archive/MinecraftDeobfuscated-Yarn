package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class GameMessageS2CPacket implements Packet<ClientPlayPacketListener> {
	private final Text message;
	private final MessageType location;
	private final UUID sender;

	public GameMessageS2CPacket(Text message, MessageType location, UUID sender) {
		this.message = message;
		this.location = location;
		this.sender = sender;
	}

	public GameMessageS2CPacket(PacketByteBuf buf) {
		this.message = buf.readText();
		this.location = MessageType.byId(buf.readByte());
		this.sender = buf.readUuid();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeText(this.message);
		buf.writeByte(this.location.getId());
		buf.writeUuid(this.sender);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameMessage(this);
	}

	public Text getMessage() {
		return this.message;
	}

	public MessageType getLocation() {
		return this.location;
	}

	public UUID getSender() {
		return this.sender;
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}
}
