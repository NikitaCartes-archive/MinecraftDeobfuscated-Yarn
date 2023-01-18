package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class SelectMerchantTradeC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int tradeId;

	public SelectMerchantTradeC2SPacket(int tradeId) {
		this.tradeId = tradeId;
	}

	public SelectMerchantTradeC2SPacket(PacketByteBuf buf) {
		this.tradeId = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.tradeId);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onSelectMerchantTrade(this);
	}

	public int getTradeId() {
		return this.tradeId;
	}
}
