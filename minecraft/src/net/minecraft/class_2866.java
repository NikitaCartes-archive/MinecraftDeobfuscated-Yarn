package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2866 implements Packet<ServerPlayPacketListener> {
	private int field_13050;
	private int field_13049;

	public class_2866() {
	}

	@Environment(EnvType.CLIENT)
	public class_2866(int i, int j) {
		this.field_13050 = i;
		this.field_13049 = j;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13050 = packetByteBuf.readVarInt();
		this.field_13049 = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.field_13050);
		packetByteBuf.writeVarInt(this.field_13049);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12057(this);
	}

	public int method_12436() {
		return this.field_13050;
	}

	public int method_12435() {
		return this.field_13049;
	}
}
