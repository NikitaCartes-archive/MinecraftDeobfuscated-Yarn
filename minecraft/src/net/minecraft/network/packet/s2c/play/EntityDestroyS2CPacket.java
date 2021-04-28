package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityDestroyS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int entityId;

	public EntityDestroyS2CPacket(int entityId) {
		this.entityId = entityId;
	}

	public EntityDestroyS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityDestroy(this);
	}

	public int getEntityId() {
		return this.entityId;
	}
}
