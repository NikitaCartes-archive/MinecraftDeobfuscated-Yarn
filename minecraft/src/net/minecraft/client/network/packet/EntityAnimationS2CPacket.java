package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityAnimationS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private int animationId;

	public EntityAnimationS2CPacket() {
	}

	public EntityAnimationS2CPacket(Entity entity, int animationId) {
		this.id = entity.getEntityId();
		this.animationId = animationId;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.animationId = buf.readUnsignedByte();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeByte(this.animationId);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityAnimation(this);
	}

	@Environment(EnvType.CLIENT)
	public int getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public int getAnimationId() {
		return this.animationId;
	}
}
