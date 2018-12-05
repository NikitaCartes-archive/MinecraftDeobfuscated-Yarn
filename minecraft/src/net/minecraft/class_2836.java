package net.minecraft;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2836 implements Packet<ServerPlayPacketListener> {
	private boolean field_12907;
	private boolean field_12906;

	public class_2836() {
	}

	public class_2836(boolean bl, boolean bl2) {
		this.field_12907 = bl;
		this.field_12906 = bl2;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12907 = packetByteBuf.readBoolean();
		this.field_12906 = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBoolean(this.field_12907);
		packetByteBuf.writeBoolean(this.field_12906);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12064(this);
	}

	public boolean method_12284() {
		return this.field_12907;
	}

	public boolean method_12285() {
		return this.field_12906;
	}
}
