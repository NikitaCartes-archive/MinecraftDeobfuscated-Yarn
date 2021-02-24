package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class class_5903 implements Packet<ClientPlayPacketListener> {
	private final Text field_29165;

	public class_5903(Text text) {
		this.field_29165 = text;
	}

	public class_5903(PacketByteBuf packetByteBuf) {
		this.field_29165 = packetByteBuf.readText();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeText(this.field_29165);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_34082(this);
	}

	@Environment(EnvType.CLIENT)
	public Text method_34190() {
		return this.field_29165;
	}
}