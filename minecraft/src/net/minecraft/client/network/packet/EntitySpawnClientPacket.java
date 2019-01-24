package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EntitySpawnClientPacket implements Packet<ClientPlayPacketListener> {
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
	private int entityTypeId;
	private int entityData;

	public EntitySpawnClientPacket() {
	}

	public EntitySpawnClientPacket(Entity entity, int i) {
		this(entity, i, 0);
	}

	public EntitySpawnClientPacket(Entity entity, int i, int j) {
		this.id = entity.getEntityId();
		this.uuid = entity.getUuid();
		this.x = entity.x;
		this.y = entity.y;
		this.z = entity.z;
		this.pitch = MathHelper.floor(entity.pitch * 256.0F / 360.0F);
		this.yaw = MathHelper.floor(entity.yaw * 256.0F / 360.0F);
		this.entityTypeId = i;
		this.entityData = j;
		double d = 3.9;
		this.velocityX = (int)(MathHelper.clamp(entity.velocityX, -3.9, 3.9) * 8000.0);
		this.velocityY = (int)(MathHelper.clamp(entity.velocityY, -3.9, 3.9) * 8000.0);
		this.velocityZ = (int)(MathHelper.clamp(entity.velocityZ, -3.9, 3.9) * 8000.0);
	}

	public EntitySpawnClientPacket(Entity entity, int i, int j, BlockPos blockPos) {
		this(entity, i, j);
		this.x = (double)blockPos.getX();
		this.y = (double)blockPos.getY();
		this.z = (double)blockPos.getZ();
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
		this.uuid = packetByteBuf.readUuid();
		this.entityTypeId = packetByteBuf.readByte();
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
		packetByteBuf.writeByte(this.entityTypeId);
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
	public int getVelocityX() {
		return this.velocityX;
	}

	@Environment(EnvType.CLIENT)
	public int getVelocityY() {
		return this.velocityY;
	}

	@Environment(EnvType.CLIENT)
	public int getVelocityz() {
		return this.velocityZ;
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
	public int getEntityTypeId() {
		return this.entityTypeId;
	}

	@Environment(EnvType.CLIENT)
	public int getEntityData() {
		return this.entityData;
	}

	public void setVelocityX(int i) {
		this.velocityX = i;
	}

	public void setVelocityY(int i) {
		this.velocityY = i;
	}

	public void setVelocityZ(int i) {
		this.velocityZ = i;
	}

	@Environment(EnvType.CLIENT)
	public void setEntityData(int i) {
		this.entityData = i;
	}
}
