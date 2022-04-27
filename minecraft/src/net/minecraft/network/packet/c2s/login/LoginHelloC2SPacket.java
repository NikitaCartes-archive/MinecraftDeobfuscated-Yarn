package net.minecraft.network.packet.c2s.login;

import java.util.Optional;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.listener.ServerLoginPacketListener;

public record LoginHelloC2SPacket(String name, Optional<PlayerPublicKey> publicKey) implements Packet<ServerLoginPacketListener> {
	public LoginHelloC2SPacket(PacketByteBuf buf) {
		this(buf.readString(16), buf.readOptional(packetByteBuf -> packetByteBuf.decode(PlayerPublicKey.CODEC)));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.name, 16);
		buf.writeOptional(this.publicKey, (packetByteBuf, playerPublicKey) -> packetByteBuf.encode(PlayerPublicKey.CODEC, playerPublicKey));
	}

	public void apply(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.onHello(this);
	}
}
