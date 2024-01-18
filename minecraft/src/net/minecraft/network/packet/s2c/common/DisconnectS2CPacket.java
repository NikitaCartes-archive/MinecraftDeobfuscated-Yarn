package net.minecraft.network.packet.s2c.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.text.Text;

public class DisconnectS2CPacket implements Packet<ClientCommonPacketListener> {
	public static final PacketCodec<PacketByteBuf, DisconnectS2CPacket> CODEC = Packet.createCodec(DisconnectS2CPacket::write, DisconnectS2CPacket::new);
	private final Text reason;

	public DisconnectS2CPacket(Text reason) {
		this.reason = reason;
	}

	private DisconnectS2CPacket(PacketByteBuf buf) {
		this.reason = buf.readUnlimitedText();
	}

	private void write(PacketByteBuf buf) {
		buf.writeText(this.reason);
	}

	@Override
	public PacketType<DisconnectS2CPacket> getPacketId() {
		return CommonPackets.DISCONNECT;
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onDisconnect(this);
	}

	public Text getReason() {
		return this.reason;
	}
}
