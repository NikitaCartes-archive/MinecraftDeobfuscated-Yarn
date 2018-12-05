package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2851 implements Packet<ServerPlayPacketListener> {
	private float field_12995;
	private float field_12994;
	private boolean field_12997;
	private boolean field_12996;

	public class_2851() {
	}

	@Environment(EnvType.CLIENT)
	public class_2851(float f, float g, boolean bl, boolean bl2) {
		this.field_12995 = f;
		this.field_12994 = g;
		this.field_12997 = bl;
		this.field_12996 = bl2;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12995 = packetByteBuf.readFloat();
		this.field_12994 = packetByteBuf.readFloat();
		byte b = packetByteBuf.readByte();
		this.field_12997 = (b & 1) > 0;
		this.field_12996 = (b & 2) > 0;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeFloat(this.field_12995);
		packetByteBuf.writeFloat(this.field_12994);
		byte b = 0;
		if (this.field_12997) {
			b = (byte)(b | 1);
		}

		if (this.field_12996) {
			b = (byte)(b | 2);
		}

		packetByteBuf.writeByte(b);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12067(this);
	}

	public float method_12372() {
		return this.field_12995;
	}

	public float method_12373() {
		return this.field_12994;
	}

	public boolean method_12371() {
		return this.field_12997;
	}

	public boolean method_12370() {
		return this.field_12996;
	}
}
