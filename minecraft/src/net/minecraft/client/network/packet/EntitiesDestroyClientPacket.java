package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntitiesDestroyClientPacket implements Packet<ClientPlayPacketListener> {
	private int[] entityIds;

	public EntitiesDestroyClientPacket() {
	}

	public EntitiesDestroyClientPacket(int... is) {
		this.entityIds = is;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.entityIds = new int[packetByteBuf.readVarInt()];

		for (int i = 0; i < this.entityIds.length; i++) {
			this.entityIds[i] = packetByteBuf.readVarInt();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.entityIds.length);

		for (int i : this.entityIds) {
			packetByteBuf.writeVarInt(i);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntitiesDestroy(this);
	}

	@Environment(EnvType.CLIENT)
	public int[] getEntityIds() {
		return this.entityIds;
	}
}
