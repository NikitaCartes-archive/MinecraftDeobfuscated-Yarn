package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class TitleS2CPacket implements Packet<ClientPlayPacketListener> {
	private final Text title;

	public TitleS2CPacket(Text title) {
		this.title = title;
	}

	public TitleS2CPacket(PacketByteBuf buf) {
		this.title = buf.readText();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeText(this.title);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTitle(this);
	}

	@Environment(EnvType.CLIENT)
	public Text getTitle() {
		return this.title;
	}
}
