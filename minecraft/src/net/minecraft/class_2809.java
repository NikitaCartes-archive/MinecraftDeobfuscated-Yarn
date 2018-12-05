package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2809 implements Packet<ServerPlayPacketListener> {
	private int field_12811;
	private short field_12809;
	private boolean field_12810;

	public class_2809() {
	}

	@Environment(EnvType.CLIENT)
	public class_2809(int i, short s, boolean bl) {
		this.field_12811 = i;
		this.field_12809 = s;
		this.field_12810 = bl;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12079(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12811 = packetByteBuf.readByte();
		this.field_12809 = packetByteBuf.readShort();
		this.field_12810 = packetByteBuf.readByte() != 0;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.field_12811);
		packetByteBuf.writeShort(this.field_12809);
		packetByteBuf.writeByte(this.field_12810 ? 1 : 0);
	}

	public int method_12178() {
		return this.field_12811;
	}

	public short method_12176() {
		return this.field_12809;
	}
}
