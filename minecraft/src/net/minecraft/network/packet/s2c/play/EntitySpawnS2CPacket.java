package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntitySpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, EntitySpawnS2CPacket> CODEC = Packet.createCodec(EntitySpawnS2CPacket::write, EntitySpawnS2CPacket::new);
	private static final double VELOCITY_SCALE = 8000.0;
	/**
	 * The maximum absolute value allowed for each scalar value (velocity x, y, z)
	 * in the velocity vector sent by this packet.
	 */
	private static final double MAX_ABSOLUTE_VELOCITY = 3.9;
	private final int entityId;
	private final UUID uuid;
	private final EntityType<?> entityType;
	private final double x;
	private final double y;
	private final double z;
	private final int velocityX;
	private final int velocityY;
	private final int velocityZ;
	private final byte pitch;
	private final byte yaw;
	private final byte headYaw;
	private final int entityData;

	public EntitySpawnS2CPacket(Entity entity, EntityTrackerEntry entityTrackerEntry) {
		this(entity, entityTrackerEntry, 0);
	}

	public EntitySpawnS2CPacket(Entity entity, EntityTrackerEntry entityTrackerEntry, int entityData) {
		this(
			entity.getId(),
			entity.getUuid(),
			entityTrackerEntry.getPos().getX(),
			entityTrackerEntry.getPos().getY(),
			entityTrackerEntry.getPos().getZ(),
			entityTrackerEntry.getPitch(),
			entityTrackerEntry.getYaw(),
			entity.getType(),
			entityData,
			entityTrackerEntry.getVelocity(),
			(double)entityTrackerEntry.getHeadYaw()
		);
	}

	public EntitySpawnS2CPacket(Entity entity, int entityData, BlockPos pos) {
		this(
			entity.getId(),
			entity.getUuid(),
			(double)pos.getX(),
			(double)pos.getY(),
			(double)pos.getZ(),
			entity.getPitch(),
			entity.getYaw(),
			entity.getType(),
			entityData,
			entity.getVelocity(),
			(double)entity.getHeadYaw()
		);
	}

	public EntitySpawnS2CPacket(
		int entityId, UUID uuid, double x, double y, double z, float pitch, float yaw, EntityType<?> entityType, int entityData, Vec3d velocity, double headYaw
	) {
		this.entityId = entityId;
		this.uuid = uuid;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = (byte)MathHelper.floor(pitch * 256.0F / 360.0F);
		this.yaw = (byte)MathHelper.floor(yaw * 256.0F / 360.0F);
		this.headYaw = (byte)MathHelper.floor(headYaw * 256.0 / 360.0);
		this.entityType = entityType;
		this.entityData = entityData;
		this.velocityX = (int)(MathHelper.clamp(velocity.x, -3.9, 3.9) * 8000.0);
		this.velocityY = (int)(MathHelper.clamp(velocity.y, -3.9, 3.9) * 8000.0);
		this.velocityZ = (int)(MathHelper.clamp(velocity.z, -3.9, 3.9) * 8000.0);
	}

	private EntitySpawnS2CPacket(RegistryByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.uuid = buf.readUuid();
		this.entityType = PacketCodecs.registryValue(RegistryKeys.ENTITY_TYPE).decode(buf);
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.pitch = buf.readByte();
		this.yaw = buf.readByte();
		this.headYaw = buf.readByte();
		this.entityData = buf.readVarInt();
		this.velocityX = buf.readShort();
		this.velocityY = buf.readShort();
		this.velocityZ = buf.readShort();
	}

	private void write(RegistryByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeUuid(this.uuid);
		PacketCodecs.registryValue(RegistryKeys.ENTITY_TYPE).encode(buf, this.entityType);
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeByte(this.pitch);
		buf.writeByte(this.yaw);
		buf.writeByte(this.headYaw);
		buf.writeVarInt(this.entityData);
		buf.writeShort(this.velocityX);
		buf.writeShort(this.velocityY);
		buf.writeShort(this.velocityZ);
	}

	@Override
	public PacketType<EntitySpawnS2CPacket> getPacketId() {
		return PlayPackets.ADD_ENTITY;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntitySpawn(this);
	}

	public int getEntityId() {
		return this.entityId;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public EntityType<?> getEntityType() {
		return this.entityType;
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

	public float getPitch() {
		return (float)(this.pitch * 360) / 256.0F;
	}

	public float getYaw() {
		return (float)(this.yaw * 360) / 256.0F;
	}

	public float getHeadYaw() {
		return (float)(this.headYaw * 360) / 256.0F;
	}

	public int getEntityData() {
		return this.entityData;
	}
}
