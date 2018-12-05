package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2805 implements Packet<ServerPlayPacketListener> {
	private int field_12784;
	private String field_12785;

	public class_2805() {
	}

	@Environment(EnvType.CLIENT)
	public class_2805(int i, String string) {
		this.field_12784 = i;
		this.field_12785 = string;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12784 = packetByteBuf.readVarInt();
		this.field_12785 = packetByteBuf.readString(32500);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.field_12784);
		packetByteBuf.writeString(this.field_12785, 32500);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12059(this);
	}

	public int method_12149() {
		return this.field_12784;
	}

	public String method_12148() {
		return this.field_12785;
	}
}
