package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class EntitiesDestroyS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, EntitiesDestroyS2CPacket> CODEC = Packet.createCodec(
		EntitiesDestroyS2CPacket::write, EntitiesDestroyS2CPacket::new
	);
	private final IntList entityIds;

	public EntitiesDestroyS2CPacket(IntList entityIds) {
		this.entityIds = new IntArrayList(entityIds);
	}

	public EntitiesDestroyS2CPacket(int... entityIds) {
		this.entityIds = new IntArrayList(entityIds);
	}

	private EntitiesDestroyS2CPacket(PacketByteBuf buf) {
		this.entityIds = buf.readIntList();
	}

	private void write(PacketByteBuf buf) {
		buf.writeIntList(this.entityIds);
	}

	@Override
	public PacketType<EntitiesDestroyS2CPacket> getPacketId() {
		return PlayPackets.REMOVE_ENTITIES;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntitiesDestroy(this);
	}

	public IntList getEntityIds() {
		return this.entityIds;
	}
}
