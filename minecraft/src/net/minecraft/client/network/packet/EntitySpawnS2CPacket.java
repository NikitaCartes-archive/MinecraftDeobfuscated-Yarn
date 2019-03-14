package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
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

	public EntitySpawnS2CPacket(int i, UUID uUID, double d, double e, double f, float g, float h, EntityType<?> entityType, int j, Vec3d vec3d) {
		this.id = i;
		this.uuid = uUID;
		this.x = d;
		this.y = e;
		this.z = f;
		this.pitch = MathHelper.floor(g * 256.0F / 360.0F);
		this.yaw = MathHelper.floor(h * 256.0F / 360.0F);
		this.entityTypeId = entityType;
		this.entityData = j;
		this.velocityX = (int)(MathHelper.clamp(vec3d.x, -3.9, 3.9) * 8000.0);
		this.velocityY = (int)(MathHelper.clamp(vec3d.y, -3.9, 3.9) * 8000.0);
		this.velocityZ = (int)(MathHelper.clamp(vec3d.z, -3.9, 3.9) * 8000.0);
	}

	public EntitySpawnS2CPacket(Entity entity) {
		this(entity, 0);
	}

	public EntitySpawnS2CPacket(Entity entity, int i) {
		this(entity.getEntityId(), entity.getUuid(), entity.x, entity.y, entity.z, entity.pitch, entity.yaw, entity.getType(), i, entity.getVelocity());
	}

	public EntitySpawnS2CPacket(Entity entity, EntityType<?> entityType, int i, BlockPos blockPos) {
		this(
			entity.getEntityId(),
			entity.getUuid(),
			(double)blockPos.getX(),
			(double)blockPos.getY(),
			(double)blockPos.getZ(),
			entity.pitch,
			entity.yaw,
			entityType,
			i,
			entity.getVelocity()
		);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
		this.uuid = packetByteBuf.readUuid();
		this.entityTypeId = Registry.ENTITY_TYPE.get(packetByteBuf.readVarInt());
		this.x = packetByteBuf.readDouble();
		this.y = packetByteBuf.readDouble();
		this.z = packetByteBuf.readDouble();
		this.pitch = packetByteBuf.readByte();
		this.yaw = packetByteBuf.readByte();
		this.entityData = packetByteBuf.readInt();
		this.velocityX = packetByteBuf.readShort();
		this.velocityY = packetByteBuf.readShort();
		this.velocityZ = packetByteBuf.readShort();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.id);
		packetByteBuf.writeUuid(this.uuid);
		packetByteBuf.writeVarInt(Registry.ENTITY_TYPE.getRawId(this.entityTypeId));
		packetByteBuf.writeDouble(this.x);
		packetByteBuf.writeDouble(this.y);
		packetByteBuf.writeDouble(this.z);
		packetByteBuf.writeByte(this.pitch);
		packetByteBuf.writeByte(this.yaw);
		packetByteBuf.writeInt(this.entityData);
		packetByteBuf.writeShort(this.velocityX);
		packetByteBuf.writeShort(this.velocityY);
		packetByteBuf.writeShort(this.velocityZ);
	}

	public void method_11178(ClientPlayPacketListener clientPlayPacketListener) {
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
	public double getVelocityz() {
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
