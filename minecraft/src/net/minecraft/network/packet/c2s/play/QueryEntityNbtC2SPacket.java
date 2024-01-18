package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class QueryEntityNbtC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, QueryEntityNbtC2SPacket> CODEC = Packet.createCodec(
		QueryEntityNbtC2SPacket::write, QueryEntityNbtC2SPacket::new
	);
	private final int transactionId;
	private final int entityId;

	public QueryEntityNbtC2SPacket(int transactionId, int entityId) {
		this.transactionId = transactionId;
		this.entityId = entityId;
	}

	private QueryEntityNbtC2SPacket(PacketByteBuf buf) {
		this.transactionId = buf.readVarInt();
		this.entityId = buf.readVarInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.transactionId);
		buf.writeVarInt(this.entityId);
	}

	@Override
	public PacketType<QueryEntityNbtC2SPacket> getPacketId() {
		return PlayPackets.ENTITY_TAG_QUERY;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onQueryEntityNbt(this);
	}

	public int getTransactionId() {
		return this.transactionId;
	}

	public int getEntityId() {
		return this.entityId;
	}
}
