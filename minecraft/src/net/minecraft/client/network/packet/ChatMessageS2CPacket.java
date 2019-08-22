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

	public ChatMessageS2CPacket(Text text) {
		this(text, MessageType.SYSTEM);
	}

	public ChatMessageS2CPacket(Text text, MessageType messageType) {
		this.message = text;
		this.location = messageType;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.message = packetByteBuf.readText();
		this.location = MessageType.byId(packetByteBuf.readByte());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeText(this.message);
		packetByteBuf.writeByte(this.location.getId());
	}

	public void method_11386(ClientPlayPacketListener clientPlayPacketListener) {
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
