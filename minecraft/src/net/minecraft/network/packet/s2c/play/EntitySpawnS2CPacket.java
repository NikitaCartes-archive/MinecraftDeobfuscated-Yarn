package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
	private int id;
	private UUID uuid;
	private double x;
	private double y;
	private double z;
	private int velocityX;
	private int velocityY;
	private int velocityZ;
	private int pitch;
	private int yaw;
	private EntityType<?> entityTypeId;
	private int entityData;

	public EntitySpawnS2CPacket() {
	}

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
			entity.getEntityId(),
			entity.getUuid(),
			entity.getX(),
			entity.getY(),
			entity.getZ(),
			entity.pitch,
			entity.yaw,
			entity.getType(),
			entityData,
			entity.getVelocity()
		);
	}

	public EntitySpawnS2CPacket(Entity entity, EntityType<?> entityType, int data, BlockPos pos) {
		this(
			entity.getEntityId(),
			entity.getUuid(),
			(double)pos.getX(),
			(double)pos.getY(),
			(double)pos.getZ(),
			entity.pitch,
			entity.yaw,
			entityType,
			data,
			entity.getVelocity()
		);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.uuid = buf.readUuid();
		this.entityTypeId = Registry.ENTITY_TYPE.get(buf.readVarInt());
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
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeUuid(this.uuid);
		buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(this.entityTypeId));
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

	@Environment(EnvType.CLIENT)
	public int getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public UUID getUuid() {
		return this.uuid;
	}

	@Environment(EnvType.CLIENT)
	public double getX() {
		return this.x;
	}

	@Environment(EnvType.CLIENT)
	public double getY() {
		return this.y;
	}

	@Environment(EnvType.CLIENT)
	public double getZ() {
		return this.z;
	}

	@Environment(EnvType.CLIENT)
	public double getVelocityX() {
		return (double)this.velocityX / 8000.0;
	}

	@Environment(EnvType.CLIENT)
	public double getVelocityY() {
		return (double)this.velocityY / 8000.0;
	}

	@Environment(EnvType.CLIENT)
	public double getVelocityZ() {
		return (double)this.velocityZ / 8000.0;
	}

	@Environment(EnvType.CLIENT)
	public int getPitch() {
		return this.pitch;
	}

	@Environment(EnvType.CLIENT)
	public int getYaw() {
		return this.yaw;
	}

	@Environment(EnvType.CLIENT)
	public EntityType<?> getEntityTypeId() {
		return this.entityTypeId;
	}

	@Environment(EnvType.CLIENT)
	public int getEntityData() {
		return this.entityData;
	}
}
