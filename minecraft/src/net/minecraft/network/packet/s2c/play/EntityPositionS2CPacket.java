package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.math.Vec3d;

public class EntityPositionS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, EntityPositionS2CPacket> CODEC = Packet.createCodec(
		EntityPositionS2CPacket::write, EntityPositionS2CPacket::new
	);
	private final int entityId;
	private final double x;
	private final double y;
	private final double z;
	private final byte yaw;
	private final byte pitch;
	private final boolean onGround;

	public EntityPositionS2CPacket(Entity entity) {
		this.entityId = entity.getId();
		Vec3d vec3d = entity.getSyncedPos();
		this.x = vec3d.x;
		this.y = vec3d.y;
		this.z = vec3d.z;
		this.yaw = (byte)((int)(entity.getYaw() * 256.0F / 360.0F));
		this.pitch = (byte)((int)(entity.getPitch() * 256.0F / 360.0F));
		this.onGround = entity.isOnGround();
	}

	private EntityPositionS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.yaw = buf.readByte();
		this.pitch = buf.readByte();
		this.onGround = buf.readBoolean();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeByte(this.yaw);
		buf.writeByte(this.pitch);
		buf.writeBoolean(this.onGround);
	}

	@Override
	public PacketType<EntityPositionS2CPacket> getPacketId() {
		return PlayPackets.TELEPORT_ENTITY;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityPosition(this);
	}

	public int getEntityId() {
		return this.entityId;
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

	public byte getYaw() {
		return this.yaw;
	}

	public byte getPitch() {
		return this.pitch;
	}

	public boolean isOnGround() {
		return this.onGround;
	}
}
