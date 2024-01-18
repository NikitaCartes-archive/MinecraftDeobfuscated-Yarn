package net.minecraft.network.packet.s2c.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public record ServerTransferS2CPacket(String host, int port) implements Packet<ClientCommonPacketListener> {
	public static final PacketCodec<PacketByteBuf, ServerTransferS2CPacket> CODEC = Packet.createCodec(
		ServerTransferS2CPacket::write, ServerTransferS2CPacket::new
	);

	private ServerTransferS2CPacket(PacketByteBuf buf) {
		this(buf.readString(), buf.readVarInt());
	}

	private void write(PacketByteBuf buf) {
		buf.writeString(this.host);
		buf.writeVarInt(this.port);
	}

	@Override
	public PacketType<ServerTransferS2CPacket> getPacketId() {
		return CommonPackets.TRANSFER;
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onServerTransfer(this);
	}
}
