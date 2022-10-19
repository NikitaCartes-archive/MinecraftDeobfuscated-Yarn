package net.minecraft.network.packet.c2s.login;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.listener.ServerLoginPacketListener;

public record LoginHelloC2SPacket(String name, PublicPlayerSession.Serialized session, Optional<UUID> profileId) implements Packet<ServerLoginPacketListener> {
	public LoginHelloC2SPacket(PacketByteBuf buf) {
		this(buf.readString(16), PublicPlayerSession.Serialized.fromBuf(buf), buf.readOptional(PacketByteBuf::readUuid));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.name, 16);
		PublicPlayerSession.Serialized.write(buf, this.session);
		buf.writeOptional(this.profileId, PacketByteBuf::writeUuid);
	}

	public void apply(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.onHello(this);
	}
}
