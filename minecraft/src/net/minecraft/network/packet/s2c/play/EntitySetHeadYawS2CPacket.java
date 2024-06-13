package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.World;

public class EntitySetHeadYawS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, EntitySetHeadYawS2CPacket> CODEC = Packet.createCodec(
		EntitySetHeadYawS2CPacket::write, EntitySetHeadYawS2CPacket::new
	);
	private final int entityId;
	private final byte headYaw;

	public EntitySetHeadYawS2CPacket(Entity entity, byte headYaw) {
		this.entityId = entity.getId();
		this.headYaw = headYaw;
	}

	private EntitySetHeadYawS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.headYaw = buf.readByte();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeByte(this.headYaw);
	}

	@Override
	public PacketType<EntitySetHeadYawS2CPacket> getPacketId() {
		return PlayPackets.ROTATE_HEAD;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntitySetHeadYaw(this);
	}

	public Entity getEntity(World world) {
		return world.getEntityById(this.entityId);
	}

	public byte getHeadYaw() {
		return this.headYaw;
	}
}
