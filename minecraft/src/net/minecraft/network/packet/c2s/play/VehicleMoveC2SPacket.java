package net.minecraft.network.packet.c2s.play;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class VehicleMoveC2SPacket implements Packet<ServerPlayPacketListener> {
	private final double x;
	private final double y;
	private final double z;
	private final float yaw;
	private final float pitch;

	public VehicleMoveC2SPacket(Entity entity) {
		this.x = entity.getX();
		this.y = entity.getY();
		this.z = entity.getZ();
		this.yaw = entity.yaw;
		this.pitch = entity.pitch;
	}

	public VehicleMoveC2SPacket(PacketByteBuf packetByteBuf) {
		this.x = packetByteBuf.readDouble();
		this.y = packetByteBuf.readDouble();
		this.z = packetByteBuf.readDouble();
		this.yaw = packetByteBuf.readFloat();
		this.pitch = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeFloat(this.yaw);
		buf.writeFloat(this.pitch);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onVehicleMove(this);
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

	public float getYaw() {
		return this.yaw;
	}

	public float getPitch() {
		return this.pitch;
	}
}
