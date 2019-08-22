package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntitiesDestroyS2CPacket implements Packet<ClientPlayPacketListener> {
	private int[] entityIds;

	public EntitiesDestroyS2CPacket() {
	}

	public EntitiesDestroyS2CPacket(int... is) {
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

	public void method_11764(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntitiesDestroy(this);
	}

	@Environment(EnvType.CLIENT)
	public int[] getEntityIds() {
		return this.entityIds;
	}
}
