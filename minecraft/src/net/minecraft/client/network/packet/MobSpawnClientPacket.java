package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class MobSpawnClientPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private UUID uuid;
	private int entityTypeId;
	private double x;
	private double y;
	private double z;
	private int yaw;
	private int pitch;
	private int headPitch;
	private byte velocityX;
	private byte velocityY;
	private byte velocityZ;
	private DataTracker dataTracker;
	private List<DataTracker.Entry<?>> trackedValues;

	public MobSpawnClientPacket() {
	}

	public MobSpawnClientPacket(LivingEntity livingEntity) {
		this.id = livingEntity.getEntityId();
		this.uuid = livingEntity.getUuid();
		this.entityTypeId = Registry.ENTITY_TYPE.getRawId(livingEntity.getType());
		this.x = livingEntity.x;
		this.y = livingEntity.y;
		this.z = livingEntity.z;
		this.velocityX = (byte)((int)(livingEntity.yaw * 256.0F / 360.0F));
		this.velocityY = (byte)((int)(livingEntity.pitch * 256.0F / 360.0F));
		this.velocityZ = (byte)((int)(livingEntity.headPitch * 256.0F / 360.0F));
		double d = 3.9;
		double e = livingEntity.velocityX;
		double f = livingEntity.velocityY;
		double g = livingEntity.velocityZ;
		if (e < -3.9) {
			e = -3.9;
		}

		if (f < -3.9) {
			f = -3.9;
		}

		if (g < -3.9) {
			g = -3.9;
		}

		if (e > 3.9) {
			e = 3.9;
		}

		if (f > 3.9) {
			f = 3.9;
		}

		if (g > 3.9) {
			g = 3.9;
		}

		this.yaw = (int)(e * 8000.0);
		this.pitch = (int)(f * 8000.0);
		this.headPitch = (int)(g * 8000.0);
		this.dataTracker = livingEntity.getDataTracker();
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
		this.uuid = packetByteBuf.readUuid();
		this.entityTypeId = packetByteBuf.readVarInt();
		this.x = packetByteBuf.readDouble();
		this.y = packetByteBuf.readDouble();
		this.z = packetByteBuf.readDouble();
		this.velocityX = packetByteBuf.readByte();
		this.velocityY = packetByteBuf.readByte();
		this.velocityZ = packetByteBuf.readByte();
		this.yaw = packetByteBuf.readShort();
		this.pitch = packetByteBuf.readShort();
		this.headPitch = packetByteBuf.readShort();
		this.trackedValues = DataTracker.deserializePacket(packetByteBuf);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.id);
		packetByteBuf.writeUuid(this.uuid);
		packetByteBuf.writeVarInt(this.entityTypeId);
		packetByteBuf.writeDouble(this.x);
		packetByteBuf.writeDouble(this.y);
		packetByteBuf.writeDouble(this.z);
		packetByteBuf.writeByte(this.velocityX);
		packetByteBuf.writeByte(this.velocityY);
		packetByteBuf.writeByte(this.velocityZ);
		packetByteBuf.writeShort(this.yaw);
		packetByteBuf.writeShort(this.pitch);
		packetByteBuf.writeShort(this.headPitch);
		this.dataTracker.serializePacket(packetByteBuf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onMobSpawn(this);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public List<DataTracker.Entry<?>> getTrackedValues() {
		return this.trackedValues;
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
	public int getEntityTypeId() {
		return this.entityTypeId;
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
	public int getYaw() {
		return this.yaw;
	}

	@Environment(EnvType.CLIENT)
	public int getPitch() {
		return this.pitch;
	}

	@Environment(EnvType.CLIENT)
	public int getHeadPitch() {
		return this.headPitch;
	}

	@Environment(EnvType.CLIENT)
	public byte getVelocityX() {
		return this.velocityX;
	}

	@Environment(EnvType.CLIENT)
	public byte getVelocityY() {
		return this.velocityY;
	}

	@Environment(EnvType.CLIENT)
	public byte getVelocityZ() {
		return this.velocityZ;
	}
}
