package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class ChunkLoadDistanceS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int distance;

	public ChunkLoadDistanceS2CPacket(int distance) {
		this.distance = distance;
	}

	public ChunkLoadDistanceS2CPacket(PacketByteBuf buf) {
		this.distance = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.distance);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkLoadDistance(this);
	}

	public int getDistance() {
		return this.distance;
	}
}
