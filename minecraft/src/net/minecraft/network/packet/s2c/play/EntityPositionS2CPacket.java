package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityPositionS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int id;
	private final double x;
	private final double y;
	private final double z;
	private final byte yaw;
	private final byte pitch;
	private final boolean onGround;

	public EntityPositionS2CPacket(Entity entity) {
		this.id = entity.getId();
		this.x = entity.getX();
		this.y = entity.getY();
		this.z = entity.getZ();
		this.yaw = (byte)((int)(entity.yaw * 256.0F / 360.0F));
		this.pitch = (byte)((int)(entity.pitch * 256.0F / 360.0F));
		this.onGround = entity.isOnGround();
	}

	public EntityPositionS2CPacket(PacketByteBuf packetByteBuf) {
		this.id = packetByteBuf.readVarInt();
		this.x = packetByteBuf.readDouble();
		this.y = packetByteBuf.readDouble();
		this.z = packetByteBuf.readDouble();
		this.yaw = packetByteBuf.readByte();
		this.pitch = packetByteBuf.readByte();
		this.onGround = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeByte(this.yaw);
		buf.writeByte(this.pitch);
		buf.writeBoolean(this.onGround);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityPosition(this);
	}

	@Environment(EnvType.CLIENT)
	public int getId() {
		return this.id;
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

	@Environment(EnvType.CLIENT)
	public boolean isOnGround() {
		return this.onGround;
	}
}
