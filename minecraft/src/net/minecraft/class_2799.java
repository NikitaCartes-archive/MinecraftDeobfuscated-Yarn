package net.minecraft;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2799 implements Packet<ServerPlayPacketListener> {
	private class_2799.class_2800 field_12773;

	public class_2799() {
	}

	public class_2799(class_2799.class_2800 arg) {
		this.field_12773 = arg;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12773 = packetByteBuf.readEnumConstant(class_2799.class_2800.class);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.field_12773);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12068(this);
	}

	public class_2799.class_2800 method_12119() {
		return this.field_12773;
	}

	public static enum class_2800 {
		field_12774,
		field_12775;
	}
}
