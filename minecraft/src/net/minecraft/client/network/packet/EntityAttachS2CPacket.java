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

	public EntityAttachS2CPacket(Entity entity, @Nullable Entity entity2) {
		this.attachedId = entity.getEntityId();
		this.holdingId = entity2 != null ? entity2.getEntityId() : -1;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.attachedId = packetByteBuf.readInt();
		this.holdingId = packetByteBuf.readInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeInt(this.attachedId);
		packetByteBuf.writeInt(this.holdingId);
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
