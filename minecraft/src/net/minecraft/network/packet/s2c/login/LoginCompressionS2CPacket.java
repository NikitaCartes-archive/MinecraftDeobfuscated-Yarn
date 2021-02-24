package net.minecraft.network.packet.s2c.login;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientLoginPacketListener;

public class LoginCompressionS2CPacket implements Packet<ClientLoginPacketListener> {
	private final int compressionThreshold;

	public LoginCompressionS2CPacket(int compressionThreshold) {
		this.compressionThreshold = compressionThreshold;
	}

	public LoginCompressionS2CPacket(PacketByteBuf packetByteBuf) {
		this.compressionThreshold = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.compressionThreshold);
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onCompression(this);
	}

	@Environment(EnvType.CLIENT)
	public int getCompressionThreshold() {
		return this.compressionThreshold;
	}
}
