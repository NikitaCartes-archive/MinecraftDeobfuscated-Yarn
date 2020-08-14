package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class DisconnectS2CPacket implements Packet<ClientPlayPacketListener> {
	private Text reason;

	public DisconnectS2CPacket() {
	}

	public DisconnectS2CPacket(Text reason) {
		this.reason = reason;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.reason = buf.readText();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeText(this.reason);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onDisconnect(this);
	}

	@Environment(EnvType.CLIENT)
	public Text getReason() {
		return this.reason;
	}
}
