package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityPassengersSetS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private int[] passengerIds;

	public EntityPassengersSetS2CPacket() {
	}

	public EntityPassengersSetS2CPacket(Entity entity) {
		this.id = entity.getEntityId();
		List<Entity> list = entity.getPassengerList();
		this.passengerIds = new int[list.size()];

		for (int i = 0; i < list.size(); i++) {
			this.passengerIds[i] = ((Entity)list.get(i)).getEntityId();
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
		this.passengerIds = packetByteBuf.readIntArray();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.id);
		packetByteBuf.writeIntArray(this.passengerIds);
	}

	public void method_11842(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityPassengersSet(this);
	}

	@Environment(EnvType.CLIENT)
	public int[] getPassengerIds() {
		return this.passengerIds;
	}

	@Environment(EnvType.CLIENT)
	public int getId() {
		return this.id;
	}
}
