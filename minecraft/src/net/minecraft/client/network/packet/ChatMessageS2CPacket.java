package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ChatMessageS2CPacket implements Packet<ClientPlayPacketListener> {
	private Component message;
	private ChatMessageType location;

	public ChatMessageS2CPacket() {
	}

	public ChatMessageS2CPacket(Component component) {
		this(component, ChatMessageType.field_11735);
	}

	public ChatMessageS2CPacket(Component component, ChatMessageType chatMessageType) {
		this.message = component;
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
		clientPlayPacketListener.onChatMessage(this);
	}

	@Environment(EnvType.CLIENT)
	public Component getMessage() {
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
