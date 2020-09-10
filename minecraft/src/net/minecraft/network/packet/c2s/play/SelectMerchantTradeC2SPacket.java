package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class SelectMerchantTradeC2SPacket implements Packet<ServerPlayPacketListener> {
	private int tradeId;

	public SelectMerchantTradeC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public SelectMerchantTradeC2SPacket(int tradeId) {
		this.tradeId = tradeId;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.tradeId = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.tradeId);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onMerchantTradeSelect(this);
	}

	public int getTradeId() {
		return this.tradeId;
	}
}
