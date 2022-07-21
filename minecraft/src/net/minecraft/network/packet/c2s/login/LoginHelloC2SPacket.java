package net.minecraft.network.packet.c2s.login;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.listener.ServerLoginPacketListener;

public record LoginHelloC2SPacket(String name, Optional<PlayerPublicKey.PublicKeyData> publicKey, Optional<UUID> profileId)
	implements Packet<ServerLoginPacketListener> {
	public LoginHelloC2SPacket(PacketByteBuf buf) {
		this(buf.readString(16), buf.readOptional(PlayerPublicKey.PublicKeyData::new), buf.readOptional(PacketByteBuf::readUuid));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.name, 16);
		buf.writeOptional(this.publicKey, (buf2, publicKey) -> publicKey.write(buf));
		buf.writeOptional(this.profileId, PacketByteBuf::writeUuid);
	}

	public void apply(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.onHello(this);
	}
}
