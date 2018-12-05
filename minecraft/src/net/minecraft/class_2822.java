package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2822 implements Packet<ServerPlayPacketListener> {
	private int field_12868;
	private int field_12867;

	public class_2822() {
	}

	@Environment(EnvType.CLIENT)
	public class_2822(int i, int j) {
		this.field_12868 = i;
		this.field_12867 = j;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12868 = packetByteBuf.readVarInt();
		this.field_12867 = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.field_12868);
		packetByteBuf.writeVarInt(this.field_12867);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12074(this);
	}

	public int method_12245() {
		return this.field_12868;
	}

	public int method_12244() {
		return this.field_12867;
	}
}
