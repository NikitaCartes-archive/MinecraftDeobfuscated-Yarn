package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class LoginQueryRequestS2CPacket implements Packet<ClientLoginPacketListener> {
	private int queryId;
	private Identifier field_13187;
	private PacketByteBuf payload;

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.queryId = packetByteBuf.readVarInt();
		this.field_13187 = packetByteBuf.method_10810();
		int i = packetByteBuf.readableBytes();
		if (i >= 0 && i <= 1048576) {
			this.payload = new PacketByteBuf(packetByteBuf.readBytes(i));
		} else {
			throw new IOException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.queryId);
		packetByteBuf.method_10812(this.field_13187);
		packetByteBuf.writeBytes(this.payload.copy());
	}

	public void method_12591(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.method_12586(this);
	}

	@Environment(EnvType.CLIENT)
	public int getQueryId() {
		return this.queryId;
	}
}
