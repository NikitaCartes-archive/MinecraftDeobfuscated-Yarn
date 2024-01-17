package net.minecraft.network.packet.s2c.play;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.network.packet.PlayPackets;

public class EntityPassengersSetS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, EntityPassengersSetS2CPacket> CODEC = Packet.createCodec(
		EntityPassengersSetS2CPacket::write, EntityPassengersSetS2CPacket::new
	);
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

	private EntityPassengersSetS2CPacket(PacketByteBuf buf) {
		this.id = buf.readVarInt();
		this.passengerIds = buf.readIntArray();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeIntArray(this.passengerIds);
	}

	@Override
	public PacketIdentifier<EntityPassengersSetS2CPacket> getPacketId() {
		return PlayPackets.SET_PASSENGERS;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityPassengersSet(this);
	}

	public int[] getPassengerIds() {
		return this.passengerIds;
	}

	public int getId() {
		return this.id;
	}
}
