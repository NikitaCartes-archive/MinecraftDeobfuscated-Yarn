package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class class_5905 implements Packet<ClientPlayPacketListener> {
	private final int field_29167;
	private final int field_29168;
	private final int field_29169;

	public class_5905(int i, int j, int k) {
		this.field_29167 = i;
		this.field_29168 = j;
		this.field_29169 = k;
	}

	public class_5905(PacketByteBuf packetByteBuf) {
		this.field_29167 = packetByteBuf.readInt();
		this.field_29168 = packetByteBuf.readInt();
		this.field_29169 = packetByteBuf.readInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.field_29167);
		buf.writeInt(this.field_29168);
		buf.writeInt(this.field_29169);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_34084(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_34194() {
		return this.field_29167;
	}

	@Environment(EnvType.CLIENT)
	public int method_34195() {
		return this.field_29168;
	}

	@Environment(EnvType.CLIENT)
	public int method_34196() {
		return this.field_29169;
	}
}
