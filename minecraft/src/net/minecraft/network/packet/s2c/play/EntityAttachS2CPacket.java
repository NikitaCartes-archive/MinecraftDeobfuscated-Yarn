package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityAttachS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int attachedId;
	private final int holdingId;

	public EntityAttachS2CPacket(Entity attachedEntity, @Nullable Entity holdingEntity) {
		this.attachedId = attachedEntity.getId();
		this.holdingId = holdingEntity != null ? holdingEntity.getId() : 0;
	}

	public EntityAttachS2CPacket(PacketByteBuf packetByteBuf) {
		this.attachedId = packetByteBuf.readInt();
		this.holdingId = packetByteBuf.readInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.attachedId);
		buf.writeInt(this.holdingId);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityAttach(this);
	}

	@Environment(EnvType.CLIENT)
	public int getAttachedEntityId() {
		return this.attachedId;
	}

	@Environment(EnvType.CLIENT)
	public int getHoldingEntityId() {
		return this.holdingId;
	}
}
