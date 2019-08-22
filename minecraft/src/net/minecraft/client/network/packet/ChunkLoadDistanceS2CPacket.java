package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ChunkLoadDistanceS2CPacket implements Packet<ClientPlayPacketListener> {
	private int distance;

	public ChunkLoadDistanceS2CPacket() {
	}

	public ChunkLoadDistanceS2CPacket(int i) {
		this.distance = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.distance = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.distance);
	}

	public void method_20205(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.handleChunkLoadDistance(this);
	}

	@Environment(EnvType.CLIENT)
	public int getDistance() {
		return this.distance;
	}
}
