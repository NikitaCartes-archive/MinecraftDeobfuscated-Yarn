package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class class_2899 implements Packet<ClientLoginPacketListener> {
	private int field_13188;
	private Identifier field_13187;
	private PacketByteBuf field_13189;

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13188 = packetByteBuf.readVarInt();
		this.field_13187 = packetByteBuf.readIdentifier();
		int i = packetByteBuf.readableBytes();
		if (i >= 0 && i <= 1048576) {
			this.field_13189 = new PacketByteBuf(packetByteBuf.readBytes(i));
		} else {
			throw new IOException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.field_13188);
		packetByteBuf.writeIdentifier(this.field_13187);
		packetByteBuf.writeBytes(this.field_13189.copy());
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.method_12586(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_12592() {
		return this.field_13188;
	}
}
