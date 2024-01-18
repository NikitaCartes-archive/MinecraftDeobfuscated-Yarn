package net.minecraft.network.packet.s2c.login;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.LoginPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public class LoginCompressionS2CPacket implements Packet<ClientLoginPacketListener> {
	public static final PacketCodec<PacketByteBuf, LoginCompressionS2CPacket> CODEC = Packet.createCodec(
		LoginCompressionS2CPacket::write, LoginCompressionS2CPacket::new
	);
	private final int compressionThreshold;

	public LoginCompressionS2CPacket(int compressionThreshold) {
		this.compressionThreshold = compressionThreshold;
	}

	private LoginCompressionS2CPacket(PacketByteBuf buf) {
		this.compressionThreshold = buf.readVarInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.compressionThreshold);
	}

	@Override
	public PacketType<LoginCompressionS2CPacket> getPacketId() {
		return LoginPackets.LOGIN_COMPRESSION;
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onCompression(this);
	}

	public int getCompressionThreshold() {
		return this.compressionThreshold;
	}
}
