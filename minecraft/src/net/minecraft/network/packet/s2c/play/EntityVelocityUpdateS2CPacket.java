package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
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
	private final int id;
	private final int velocityX;
	private final int velocityY;
	private final int velocityZ;

	public EntityVelocityUpdateS2CPacket(Entity entity) {
		this(entity.getId(), entity.getVelocity());
	}

	public EntityVelocityUpdateS2CPacket(int id, Vec3d velocity) {
		this.id = id;
		double d = 3.9;
		double e = MathHelper.clamp(velocity.x, -3.9, 3.9);
		double f = MathHelper.clamp(velocity.y, -3.9, 3.9);
		double g = MathHelper.clamp(velocity.z, -3.9, 3.9);
		this.velocityX = (int)(e * 8000.0);
		this.velocityY = (int)(f * 8000.0);
		this.velocityZ = (int)(g * 8000.0);
	}

	public EntityVelocityUpdateS2CPacket(PacketByteBuf buf) {
		this.id = buf.readVarInt();
		this.velocityX = buf.readShort();
		this.velocityY = buf.readShort();
		this.velocityZ = buf.readShort();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeShort(this.velocityX);
		buf.writeShort(this.velocityY);
		buf.writeShort(this.velocityZ);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityVelocityUpdate(this);
	}

	public int getId() {
		return this.id;
	}

	public int getVelocityX() {
		return this.velocityX;
	}

	public int getVelocityY() {
		return this.velocityY;
	}

	public int getVelocityZ() {
		return this.velocityZ;
	}
}
