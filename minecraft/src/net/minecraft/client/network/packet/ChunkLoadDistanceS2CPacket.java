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

	public ChunkLoadDistanceS2CPacket(int distance) {
		this.distance = distance;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.distance = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.distance);
	}

	public void method_20205(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkLoadDistance(this);
	}

	@Environment(EnvType.CLIENT)
	public int getDistance() {
		return this.distance;
	}
}
