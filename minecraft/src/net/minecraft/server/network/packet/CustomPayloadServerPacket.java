package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CustomPayloadServerPacket implements Packet<ServerPlayPacketListener> {
	public static final Identifier BRAND = new Identifier("brand");
	private Identifier channel;
	private PacketByteBuf data;

	public CustomPayloadServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public CustomPayloadServerPacket(Identifier identifier, PacketByteBuf packetByteBuf) {
		this.channel = identifier;
		this.data = packetByteBuf;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.channel = packetByteBuf.readIdentifier();
		int i = packetByteBuf.readableBytes();
		if (i >= 0 && i <= 32767) {
			this.data = new PacketByteBuf(packetByteBuf.readBytes(i));
		} else {
			throw new IOException("Payload may not be larger than 32767 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeIdentifier(this.channel);
		packetByteBuf.writeBytes(this.data);
	}

	public void method_12199(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCustomPayload(this);
		if (this.data != null) {
			this.data.release();
		}
	}
}
