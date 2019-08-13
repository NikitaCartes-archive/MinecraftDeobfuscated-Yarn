package net.minecraft.server.network.packet;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class VehicleMoveC2SPacket implements Packet<ServerPlayPacketListener> {
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;

	public VehicleMoveC2SPacket() {
	}

	public VehicleMoveC2SPacket(Entity entity) {
		this.x = entity.x;
		this.y = entity.y;
		this.z = entity.z;
		this.yaw = entity.yaw;
		this.pitch = entity.pitch;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.x = packetByteBuf.readDouble();
		this.y = packetByteBuf.readDouble();
		this.z = packetByteBuf.readDouble();
		this.yaw = packetByteBuf.readFloat();
		this.pitch = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeDouble(this.x);
		packetByteBuf.writeDouble(this.y);
		packetByteBuf.writeDouble(this.z);
		packetByteBuf.writeFloat(this.yaw);
		packetByteBuf.writeFloat(this.pitch);
	}

	public void method_12278(ServerPlayPacketListener serverPlayPacketListener) {
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
