package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class SelectMerchantTradeC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, SelectMerchantTradeC2SPacket> CODEC = Packet.createCodec(
		SelectMerchantTradeC2SPacket::write, SelectMerchantTradeC2SPacket::new
	);
	private final int tradeId;

	public SelectMerchantTradeC2SPacket(int tradeId) {
		this.tradeId = tradeId;
	}

	private SelectMerchantTradeC2SPacket(PacketByteBuf buf) {
		this.tradeId = buf.readVarInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.tradeId);
	}

	@Override
	public PacketType<SelectMerchantTradeC2SPacket> getPacketId() {
		return PlayPackets.SELECT_TRADE;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onSelectMerchantTrade(this);
	}

	public int getTradeId() {
		return this.tradeId;
	}
}
