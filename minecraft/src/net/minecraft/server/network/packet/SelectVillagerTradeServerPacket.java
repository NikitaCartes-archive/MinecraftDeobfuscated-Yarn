package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class SelectVillagerTradeServerPacket implements Packet<ServerPlayPacketListener> {
	private int tradeId;

	public SelectVillagerTradeServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public SelectVillagerTradeServerPacket(int i) {
		this.tradeId = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.tradeId = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.tradeId);
	}

	public void method_12430(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onVillagerTradeSelect(this);
	}

	public int method_12431() {
		return this.tradeId;
	}
}
