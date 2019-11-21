package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;

public class ChatMessageS2CPacket implements Packet<ClientPlayPacketListener> {
	private Text message;
	private MessageType location;

	public ChatMessageS2CPacket() {
	}

	public ChatMessageS2CPacket(Text message) {
		this(message, MessageType.SYSTEM);
	}

	public ChatMessageS2CPacket(Text message, MessageType location) {
		this.message = message;
		this.location = location;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.message = buf.readText();
		this.location = MessageType.byId(buf.readByte());
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeText(this.message);
		buf.writeByte(this.location.getId());
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChatMessage(this);
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

	@Override
	public boolean isErrorFatal() {
		return true;
	}
}
