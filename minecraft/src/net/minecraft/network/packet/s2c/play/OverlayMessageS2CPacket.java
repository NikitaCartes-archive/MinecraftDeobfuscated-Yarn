package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;

public class OverlayMessageS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, OverlayMessageS2CPacket> CODEC = Packet.createCodec(
		OverlayMessageS2CPacket::write, OverlayMessageS2CPacket::new
	);
	private final Text message;

	public OverlayMessageS2CPacket(Text message) {
		this.message = message;
	}

	private OverlayMessageS2CPacket(PacketByteBuf buf) {
		this.message = buf.readUnlimitedText();
	}

	private void write(PacketByteBuf buf) {
		buf.writeText(this.message);
	}

	@Override
	public PacketType<OverlayMessageS2CPacket> getPacketId() {
		return PlayPackets.SET_ACTION_BAR_TEXT;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onOverlayMessage(this);
	}

	public Text getMessage() {
		return this.message;
	}
}
