package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class LoginCompressionS2CPacket implements Packet<ClientLoginPacketListener> {
	private int compressionThreshold;

	public LoginCompressionS2CPacket() {
	}

	public LoginCompressionS2CPacket(int i) {
		this.compressionThreshold = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.compressionThreshold = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.compressionThreshold);
	}

	public void method_12633(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onCompression(this);
	}

	@Environment(EnvType.CLIENT)
	public int getCompressionThreshold() {
		return this.compressionThreshold;
	}
}
