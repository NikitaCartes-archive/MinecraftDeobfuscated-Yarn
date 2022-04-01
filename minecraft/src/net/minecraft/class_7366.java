package net.minecraft;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.math.Vec3d;

public class class_7366 implements Packet<ServerPlayPacketListener> {
	private final Vec3d field_38681;

	public class_7366(Vec3d vec3d) {
		this.field_38681 = vec3d;
	}

	public class_7366(PacketByteBuf packetByteBuf) {
		this.field_38681 = new Vec3d(packetByteBuf.readDouble(), packetByteBuf.readDouble(), packetByteBuf.readDouble());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeDouble(this.field_38681.getX());
		buf.writeDouble(this.field_38681.getY());
		buf.writeDouble(this.field_38681.getZ());
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_42771(this);
	}

	public Vec3d method_43021() {
		return this.field_38681;
	}
}
