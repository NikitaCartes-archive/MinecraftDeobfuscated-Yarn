package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityAnimationS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int id;
	private final int animationId;

	public EntityAnimationS2CPacket(Entity entity, int animationId) {
		this.id = entity.getId();
		this.animationId = animationId;
	}

	public EntityAnimationS2CPacket(PacketByteBuf buf) {
		this.id = buf.readVarInt();
		this.animationId = buf.readUnsignedByte();
	}

	@Override
	public void write(PacketByteBuf buf) {
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
