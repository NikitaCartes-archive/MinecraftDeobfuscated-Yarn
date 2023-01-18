package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class QueryEntityNbtC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int transactionId;
	private final int entityId;

	public QueryEntityNbtC2SPacket(int transactionId, int entityId) {
		this.transactionId = transactionId;
		this.entityId = entityId;
	}

	public QueryEntityNbtC2SPacket(PacketByteBuf buf) {
		this.transactionId = buf.readVarInt();
		this.entityId = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.transactionId);
		buf.writeVarInt(this.entityId);
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
