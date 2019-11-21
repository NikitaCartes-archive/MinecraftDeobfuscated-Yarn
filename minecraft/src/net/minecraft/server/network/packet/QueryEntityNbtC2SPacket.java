package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class QueryEntityNbtC2SPacket implements Packet<ServerPlayPacketListener> {
	private int transactionId;
	private int entityId;

	public QueryEntityNbtC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public QueryEntityNbtC2SPacket(int i, int j) {
		this.transactionId = i;
		this.entityId = j;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.transactionId = buf.readVarInt();
		this.entityId = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
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
