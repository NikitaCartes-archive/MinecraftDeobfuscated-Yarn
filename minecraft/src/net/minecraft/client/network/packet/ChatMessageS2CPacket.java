package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.text.TextComponent;
import net.minecraft.util.PacketByteBuf;

public class ChatMessageS2CPacket implements Packet<ClientPlayPacketListener> {
	private TextComponent message;
	private ChatMessageType location;

	public ChatMessageS2CPacket() {
	}

	public ChatMessageS2CPacket(TextComponent textComponent) {
		this(textComponent, ChatMessageType.field_11735);
	}

	public ChatMessageS2CPacket(TextComponent textComponent, ChatMessageType chatMessageType) {
		this.message = textComponent;
		this.location = chatMessageType;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.message = packetByteBuf.readTextComponent();
		this.location = ChatMessageType.byId(packetByteBuf.readByte());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeTextComponent(this.message);
		packetByteBuf.writeByte(this.location.getId());
	}

	public void method_11386(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11121(this);
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getMessage() {
		return this.message;
	}

	public boolean isNonChat() {
		return this.location == ChatMessageType.field_11735 || this.location == ChatMessageType.field_11733;
	}

	public ChatMessageType getLocation() {
		return this.location;
	}

	@Override
	public boolean isErrorFatal() {
		return true;
	}
}
