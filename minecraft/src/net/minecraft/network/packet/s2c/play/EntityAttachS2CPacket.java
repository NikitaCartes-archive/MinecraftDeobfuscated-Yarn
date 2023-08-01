package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class EntityAttachS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int attachedEntityId;
	private final int holdingEntityId;

	public EntityAttachS2CPacket(Entity attachedEntity, @Nullable Entity holdingEntity) {
		this.attachedEntityId = attachedEntity.getId();
		this.holdingEntityId = holdingEntity != null ? holdingEntity.getId() : 0;
	}

	public EntityAttachS2CPacket(PacketByteBuf buf) {
		this.attachedEntityId = buf.readInt();
		this.holdingEntityId = buf.readInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.attachedEntityId);
		buf.writeInt(this.holdingEntityId);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityAttach(this);
	}

	public int getAttachedEntityId() {
		return this.attachedEntityId;
	}

	public int getHoldingEntityId() {
		return this.holdingEntityId;
	}
}
