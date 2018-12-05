package net.minecraft;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2833 implements Packet<ServerPlayPacketListener> {
	private double field_12899;
	private double field_12897;
	private double field_12895;
	private float field_12898;
	private float field_12896;

	public class_2833() {
	}

	public class_2833(Entity entity) {
		this.field_12899 = entity.x;
		this.field_12897 = entity.y;
		this.field_12895 = entity.z;
		this.field_12898 = entity.yaw;
		this.field_12896 = entity.pitch;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12899 = packetByteBuf.readDouble();
		this.field_12897 = packetByteBuf.readDouble();
		this.field_12895 = packetByteBuf.readDouble();
		this.field_12898 = packetByteBuf.readFloat();
		this.field_12896 = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeDouble(this.field_12899);
		packetByteBuf.writeDouble(this.field_12897);
		packetByteBuf.writeDouble(this.field_12895);
		packetByteBuf.writeFloat(this.field_12898);
		packetByteBuf.writeFloat(this.field_12896);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12078(this);
	}

	public double method_12279() {
		return this.field_12899;
	}

	public double method_12280() {
		return this.field_12897;
	}

	public double method_12276() {
		return this.field_12895;
	}

	public float method_12281() {
		return this.field_12898;
	}

	public float method_12277() {
		return this.field_12896;
	}
}
