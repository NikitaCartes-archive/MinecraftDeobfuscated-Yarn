package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class ChunkLoadDistanceS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ChunkLoadDistanceS2CPacket> CODEC = Packet.createCodec(
		ChunkLoadDistanceS2CPacket::write, ChunkLoadDistanceS2CPacket::new
	);
	private final int distance;

	public ChunkLoadDistanceS2CPacket(int distance) {
		this.distance = distance;
	}

	private ChunkLoadDistanceS2CPacket(PacketByteBuf buf) {
		this.distance = buf.readVarInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.distance);
	}

	@Override
	public PacketType<ChunkLoadDistanceS2CPacket> getPacketId() {
		return PlayPackets.SET_CHUNK_CACHE_RADIUS;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkLoadDistance(this);
	}

	public int getDistance() {
		return this.distance;
	}
}
