package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class GameMessageS2CPacket implements Packet<ClientPlayPacketListener> {
	private final Text message;
	private final MessageType location;
	private final UUID senderUuid;

	public GameMessageS2CPacket(Text message, MessageType location, UUID senderUuid) {
		this.message = message;
		this.location = location;
		this.senderUuid = senderUuid;
	}

	public GameMessageS2CPacket(PacketByteBuf buf) {
		this.message = buf.readText();
		this.location = MessageType.byId(buf.readByte());
		this.senderUuid = buf.readUuid();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeText(this.message);
		buf.writeByte(this.location.getId());
		buf.writeUuid(this.senderUuid);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameMessage(this);
	}

	@Environment(EnvType.CLIENT)
	public Text getMessage() {
		return this.message;
	}

	@Environment(EnvType.CLIENT)
	public MessageType getLocation() {
		return this.location;
	}

	@Environment(EnvType.CLIENT)
	public UUID getSenderUuid() {
		return this.senderUuid;
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}
}
