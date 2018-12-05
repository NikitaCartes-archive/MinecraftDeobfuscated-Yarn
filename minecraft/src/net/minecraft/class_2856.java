package net.minecraft;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2856 implements Packet<ServerPlayPacketListener> {
	private class_2856.class_2857 field_13014;

	public class_2856() {
	}

	public class_2856(class_2856.class_2857 arg) {
		this.field_13014 = arg;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13014 = packetByteBuf.readEnumConstant(class_2856.class_2857.class);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.field_13014);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12081(this);
	}

	public static enum class_2857 {
		field_13017,
		field_13018,
		field_13015,
		field_13016;
	}
}
