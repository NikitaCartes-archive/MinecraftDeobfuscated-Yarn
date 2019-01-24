package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class VehicleMoveClientPacket implements Packet<ClientPlayPacketListener> {
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;

	public VehicleMoveClientPacket() {
	}

	public VehicleMoveClientPacket(Entity entity) {
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

	public void method_11672(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onVehicleMove(this);
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
	public float getYaw() {
		return this.yaw;
	}

	@Environment(EnvType.CLIENT)
	public float getPitch() {
		return this.pitch;
	}
}
