package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CustomPayloadC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final Identifier field_12831 = new Identifier("brand");
	private Identifier field_12830;
	private PacketByteBuf data;

	public CustomPayloadC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public CustomPayloadC2SPacket(Identifier identifier, PacketByteBuf packetByteBuf) {
		this.field_12830 = identifier;
		this.data = packetByteBuf;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12830 = packetByteBuf.method_10810();
		int i = packetByteBuf.readableBytes();
		if (i >= 0 && i <= 32767) {
			this.data = new PacketByteBuf(packetByteBuf.readBytes(i));
		} else {
			throw new IOException("Payload may not be larger than 32767 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.method_10812(this.field_12830);
		packetByteBuf.writeBytes(this.data);
	}

	public void method_12199(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12075(this);
		if (this.data != null) {
			this.data.release();
		}
	}
}
