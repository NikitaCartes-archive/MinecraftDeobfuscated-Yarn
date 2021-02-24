package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ChunkLoadDistanceS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int distance;

	public ChunkLoadDistanceS2CPacket(int distance) {
		this.distance = distance;
	}

	public ChunkLoadDistanceS2CPacket(PacketByteBuf packetByteBuf) {
		this.distance = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.distance);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkLoadDistance(this);
	}

	@Environment(EnvType.CLIENT)
	public int getDistance() {
		return this.distance;
	}
}
