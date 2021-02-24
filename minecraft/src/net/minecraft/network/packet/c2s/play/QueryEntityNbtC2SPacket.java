package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class QueryEntityNbtC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int transactionId;
	private final int entityId;

	@Environment(EnvType.CLIENT)
	public QueryEntityNbtC2SPacket(int transactionId, int entityId) {
		this.transactionId = transactionId;
		this.entityId = entityId;
	}

	public QueryEntityNbtC2SPacket(PacketByteBuf packetByteBuf) {
		this.transactionId = packetByteBuf.readVarInt();
		this.entityId = packetByteBuf.readVarInt();
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
