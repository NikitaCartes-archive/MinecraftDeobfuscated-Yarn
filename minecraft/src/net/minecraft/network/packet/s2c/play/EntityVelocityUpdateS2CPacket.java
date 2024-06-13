package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Sent when a server modifies an entity's velocity.
 * 
 * <p>If the entity is a player, {@link
 * net.minecraft.network.packet.s2c.play.ExplosionS2CPacket} can be used as
 * a replacement.
 */
public class EntityVelocityUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, EntityVelocityUpdateS2CPacket> CODEC = Packet.createCodec(
		EntityVelocityUpdateS2CPacket::write, EntityVelocityUpdateS2CPacket::new
	);
	private final int entityId;
	private final int velocityX;
	private final int velocityY;
	private final int velocityZ;

	public EntityVelocityUpdateS2CPacket(Entity entity) {
		this(entity.getId(), entity.getVelocity());
	}

	public EntityVelocityUpdateS2CPacket(int entityId, Vec3d velocity) {
		this.entityId = entityId;
		double d = 3.9;
		double e = MathHelper.clamp(velocity.x, -3.9, 3.9);
		double f = MathHelper.clamp(velocity.y, -3.9, 3.9);
		double g = MathHelper.clamp(velocity.z, -3.9, 3.9);
		this.velocityX = (int)(e * 8000.0);
		this.velocityY = (int)(f * 8000.0);
		this.velocityZ = (int)(g * 8000.0);
	}

	private EntityVelocityUpdateS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.velocityX = buf.readShort();
		this.velocityY = buf.readShort();
		this.velocityZ = buf.readShort();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeShort(this.velocityX);
		buf.writeShort(this.velocityY);
		buf.writeShort(this.velocityZ);
	}

	@Override
	public PacketType<EntityVelocityUpdateS2CPacket> getPacketId() {
		return PlayPackets.SET_ENTITY_MOTION;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityVelocityUpdate(this);
	}

	public int getEntityId() {
		return this.entityId;
	}

	public double getVelocityX() {
		return (double)this.velocityX / 8000.0;
	}

	public double getVelocityY() {
		return (double)this.velocityY / 8000.0;
	}

	public double getVelocityZ() {
		return (double)this.velocityZ / 8000.0;
	}
}
