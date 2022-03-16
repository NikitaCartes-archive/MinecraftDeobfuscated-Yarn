package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class EntitySpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final double VELOCITY_SCALE = 8000.0;
	private final int id;
	private final UUID uuid;
	private final double x;
	private final double y;
	private final double z;
	private final int velocityX;
	private final int velocityY;
	private final int velocityZ;
	private final int pitch;
	private final int yaw;
	private final EntityType<?> entityTypeId;
	private final int entityData;
	/**
	 * The maximum absolute value allowed for each scalar value (velocity x, y, z)
	 * in the velocity vector sent by this packet.
	 */
	public static final double MAX_ABSOLUTE_VELOCITY = 3.9;

	public EntitySpawnS2CPacket(
		int id, UUID uuid, double x, double y, double z, float pitch, float yaw, EntityType<?> entityTypeId, int entityData, Vec3d velocity
	) {
		this.id = id;
		this.uuid = uuid;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = MathHelper.floor(pitch * 256.0F / 360.0F);
		this.yaw = MathHelper.floor(yaw * 256.0F / 360.0F);
		this.entityTypeId = entityTypeId;
		this.entityData = entityData;
		this.velocityX = (int)(MathHelper.clamp(velocity.x, -3.9, 3.9) * 8000.0);
		this.velocityY = (int)(MathHelper.clamp(velocity.y, -3.9, 3.9) * 8000.0);
		this.velocityZ = (int)(MathHelper.clamp(velocity.z, -3.9, 3.9) * 8000.0);
	}

	public EntitySpawnS2CPacket(Entity entity) {
		this(entity, 0);
	}

	public EntitySpawnS2CPacket(Entity entity, int entityData) {
		this(
			entity.getId(),
			entity.getUuid(),
			entity.getX(),
			entity.getY(),
			entity.getZ(),
			entity.getPitch(),
			entity.getYaw(),
			entity.getType(),
			entityData,
			entity.getVelocity()
		);
	}

	public EntitySpawnS2CPacket(Entity entity, EntityType<?> entityType, int data, BlockPos pos) {
		this(
			entity.getId(),
			entity.getUuid(),
			(double)pos.getX(),
			(double)pos.getY(),
			(double)pos.getZ(),
			entity.getPitch(),
			entity.getYaw(),
			entityType,
			data,
			entity.getVelocity()
		);
	}

	public EntitySpawnS2CPacket(PacketByteBuf buf) {
		this.id = buf.readVarInt();
		this.uuid = buf.readUuid();
		this.entityTypeId = buf.readRegistryValue(Registry.ENTITY_TYPE);
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.pitch = buf.readByte();
		this.yaw = buf.readByte();
		this.entityData = buf.readInt();
		this.velocityX = buf.readShort();
		this.velocityY = buf.readShort();
		this.velocityZ = buf.readShort();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeUuid(this.uuid);
		buf.writeRegistryValue(Registry.ENTITY_TYPE, this.entityTypeId);
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeByte(this.pitch);
		buf.writeByte(this.yaw);
		buf.writeInt(this.entityData);
		buf.writeShort(this.velocityX);
		buf.writeShort(this.velocityY);
		buf.writeShort(this.velocityZ);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntitySpawn(this);
	}

	public int getId() {
		return this.id;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
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

	public int getPitch() {
		return this.pitch;
	}

	public int getYaw() {
		return this.yaw;
	}

	public EntityType<?> getEntityTypeId() {
		return this.entityTypeId;
	}

	public int getEntityData() {
		return this.entityData;
	}
}
