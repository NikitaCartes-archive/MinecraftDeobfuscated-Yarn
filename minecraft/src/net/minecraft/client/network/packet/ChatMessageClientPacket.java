package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.text.TextComponent;
import net.minecraft.util.PacketByteBuf;

public class ChatMessageClientPacket implements Packet<ClientPlayPacketListener> {
	private TextComponent message;
	private ChatMessageType location;

	public ChatMessageClientPacket() {
	}

	public ChatMessageClientPacket(TextComponent textComponent) {
		this(textComponent, ChatMessageType.field_11735);
	}

	public ChatMessageClientPacket(TextComponent textComponent, ChatMessageType chatMessageType) {
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

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChatMessage(this);
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
