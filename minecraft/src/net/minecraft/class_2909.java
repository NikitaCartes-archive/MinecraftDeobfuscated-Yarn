package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.text.TextComponent;
import net.minecraft.util.PacketByteBuf;

public class class_2909 implements Packet<ClientLoginPacketListener> {
	private TextComponent field_13243;

	public class_2909() {
	}

	public class_2909(TextComponent textComponent) {
		this.field_13243 = textComponent;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13243 = TextComponent.Serializer.fromLenientJsonString(packetByteBuf.readString(262144));
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeTextComponent(this.field_13243);
	}

	public void method_12637(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.method_12584(this);
	}

	@Environment(EnvType.CLIENT)
	public TextComponent method_12638() {
		return this.field_13243;
	}
}
