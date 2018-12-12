package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class QueryEntityNbtServerPacket implements Packet<ServerPlayPacketListener> {
	private int transactionId;
	private int entityId;

	public QueryEntityNbtServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public QueryEntityNbtServerPacket(int i, int j) {
		this.transactionId = i;
		this.entityId = j;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.transactionId = packetByteBuf.readVarInt();
		this.entityId = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.transactionId);
		packetByteBuf.writeVarInt(this.entityId);
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
