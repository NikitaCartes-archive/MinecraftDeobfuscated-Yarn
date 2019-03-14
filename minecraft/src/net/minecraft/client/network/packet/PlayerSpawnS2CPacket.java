package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class PlayerSpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private UUID uuid;
	private double x;
	private double y;
	private double z;
	private byte yaw;
	private byte pitch;
	private DataTracker dataTracker;
	private List<DataTracker.Entry<?>> trackedValues;

	public PlayerSpawnS2CPacket() {
	}

	public PlayerSpawnS2CPacket(PlayerEntity playerEntity) {
		this.id = playerEntity.getEntityId();
		this.uuid = playerEntity.getGameProfile().getId();
		this.x = playerEntity.x;
		this.y = playerEntity.y;
		this.z = playerEntity.z;
		this.yaw = (byte)((int)(playerEntity.yaw * 256.0F / 360.0F));
		this.pitch = (byte)((int)(playerEntity.pitch * 256.0F / 360.0F));
		this.dataTracker = playerEntity.getDataTracker();
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
		this.uuid = packetByteBuf.readUuid();
		this.x = packetByteBuf.readDouble();
		this.y = packetByteBuf.readDouble();
		this.z = packetByteBuf.readDouble();
		this.yaw = packetByteBuf.readByte();
		this.pitch = packetByteBuf.readByte();
		this.trackedValues = DataTracker.deserializePacket(packetByteBuf);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.id);
		packetByteBuf.writeUuid(this.uuid);
		packetByteBuf.writeDouble(this.x);
		packetByteBuf.writeDouble(this.y);
		packetByteBuf.writeDouble(this.z);
		packetByteBuf.writeByte(this.yaw);
		packetByteBuf.writeByte(this.pitch);
		this.dataTracker.serializePacket(packetByteBuf);
	}

	public void method_11235(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerSpawn(this);
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
	public UUID getPlayerUuid() {
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
	public byte getYaw() {
		return this.yaw;
	}

	@Environment(EnvType.CLIENT)
	public byte getPitch() {
		return this.pitch;
	}
}
