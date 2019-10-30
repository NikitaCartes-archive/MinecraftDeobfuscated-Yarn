package net.minecraft.client.network.packet;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityAttachS2CPacket implements Packet<ClientPlayPacketListener> {
	private int attachedId;
	private int holdingId;

	public EntityAttachS2CPacket() {
	}

	public EntityAttachS2CPacket(Entity attachedEntity, @Nullable Entity holdingEntity) {
		this.attachedId = attachedEntity.getEntityId();
		this.holdingId = holdingEntity != null ? holdingEntity.getEntityId() : 0;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.attachedId = buf.readInt();
		this.holdingId = buf.readInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.attachedId);
		buf.writeInt(this.holdingId);
	}

	public void method_11811(ClientPlayPacketListener clientPlayPacketListener) {
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
