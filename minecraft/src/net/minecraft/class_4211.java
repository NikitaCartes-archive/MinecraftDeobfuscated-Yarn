package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_4211 implements Packet<ServerPlayPacketListener> {
	private boolean field_18806;

	public class_4211() {
	}

	@Environment(EnvType.CLIENT)
	public class_4211(boolean bl) {
		this.field_18806 = bl;
	}

	public void method_19484(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_19476(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_18806 = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBoolean(this.field_18806);
	}

	public boolean method_19485() {
		return this.field_18806;
	}
}
