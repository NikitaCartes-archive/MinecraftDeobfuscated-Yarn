package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class PlayerSpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int id;
	private final UUID uuid;
	private final double x;
	private final double y;
	private final double z;
	private final byte yaw;
	private final byte pitch;

	public PlayerSpawnS2CPacket(PlayerEntity player) {
		this.id = player.getId();
		this.uuid = player.getGameProfile().getId();
		this.x = player.getX();
		this.y = player.getY();
		this.z = player.getZ();
		this.yaw = (byte)((int)(player.yaw * 256.0F / 360.0F));
		this.pitch = (byte)((int)(player.pitch * 256.0F / 360.0F));
	}

	public PlayerSpawnS2CPacket(PacketByteBuf buf) {
		this.id = buf.readVarInt();
		this.uuid = buf.readUuid();
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.yaw = buf.readByte();
		this.pitch = buf.readByte();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeUuid(this.uuid);
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeByte(this.yaw);
		buf.writeByte(this.pitch);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerSpawn(this);
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
