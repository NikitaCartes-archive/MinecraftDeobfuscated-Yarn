package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class class_5904 implements Packet<ClientPlayPacketListener> {
	private final Text field_29166;

	public class_5904(Text text) {
		this.field_29166 = text;
	}

	public class_5904(PacketByteBuf packetByteBuf) {
		this.field_29166 = packetByteBuf.readText();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeText(this.field_29166);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_34083(this);
	}

	@Environment(EnvType.CLIENT)
	public Text method_34192() {
		return this.field_29166;
	}
}
