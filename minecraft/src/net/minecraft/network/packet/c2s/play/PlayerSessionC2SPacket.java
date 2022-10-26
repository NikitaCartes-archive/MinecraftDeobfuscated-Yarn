package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.listener.ServerPlayPacketListener;

public record PlayerSessionC2SPacket(PublicPlayerSession.Serialized chatSession) implements Packet<ServerPlayPacketListener> {
	public PlayerSessionC2SPacket(PacketByteBuf buf) {
		this(PublicPlayerSession.Serialized.fromBuf(buf));
	}

	@Override
	public void write(PacketByteBuf buf) {
		PublicPlayerSession.Serialized.write(buf, this.chatSession);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerSession(this);
	}
}
