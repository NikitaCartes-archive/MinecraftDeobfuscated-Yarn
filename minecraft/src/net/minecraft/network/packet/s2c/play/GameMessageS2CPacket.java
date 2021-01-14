package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class GameMessageS2CPacket implements Packet<ClientPlayPacketListener> {
	private Text message;
	private MessageType location;
	private UUID sender;

	public GameMessageS2CPacket() {
	}

	public GameMessageS2CPacket(Text message, MessageType location, UUID sender) {
		this.message = message;
		this.location = location;
		this.sender = sender;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.message = buf.readText();
		this.location = MessageType.byId(buf.readByte());
		this.sender = buf.readUuid();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeText(this.message);
		buf.writeByte(this.location.getId());
		buf.writeUuid(this.sender);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameMessage(this);
	}

	@Environment(EnvType.CLIENT)
	public Text getMessage() {
		return this.message;
	}

	public boolean isNonChat() {
		return this.location == MessageType.SYSTEM || this.location == MessageType.GAME_INFO;
	}

	public MessageType getLocation() {
		return this.location;
	}

	@Environment(EnvType.CLIENT)
	public UUID getSender() {
		return this.sender;
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}
}
