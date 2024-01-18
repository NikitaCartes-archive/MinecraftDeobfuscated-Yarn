package net.minecraft.network.packet.c2s.login;

import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.LoginPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public record LoginHelloC2SPacket(String name, UUID profileId) implements Packet<ServerLoginPacketListener> {
	public static final PacketCodec<PacketByteBuf, LoginHelloC2SPacket> CODEC = Packet.createCodec(LoginHelloC2SPacket::write, LoginHelloC2SPacket::new);

	private LoginHelloC2SPacket(PacketByteBuf buf) {
		this(buf.readString(16), buf.readUuid());
	}

	private void write(PacketByteBuf buf) {
		buf.writeString(this.name, 16);
		buf.writeUuid(this.profileId);
	}

	@Override
	public PacketType<LoginHelloC2SPacket> getPacketId() {
		return LoginPackets.HELLO_C2S;
	}

	public void apply(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.onHello(this);
	}
}
