package net.minecraft.network.packet.s2c.play;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityPassengersSetS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int id;
	private final int[] passengerIds;

	public EntityPassengersSetS2CPacket(Entity entity) {
		this.id = entity.getId();
		List<Entity> list = entity.getPassengerList();
		this.passengerIds = new int[list.size()];

		for (int i = 0; i < list.size(); i++) {
			this.passengerIds[i] = ((Entity)list.get(i)).getId();
		}
	}

	public EntityPassengersSetS2CPacket(PacketByteBuf packetByteBuf) {
		this.id = packetByteBuf.readVarInt();
		this.passengerIds = packetByteBuf.readIntArray();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeIntArray(this.passengerIds);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
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
