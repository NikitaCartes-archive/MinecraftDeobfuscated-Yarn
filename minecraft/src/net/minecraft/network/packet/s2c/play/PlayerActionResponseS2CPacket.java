package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public record PlayerActionResponseS2CPacket(int sequence) implements Packet<ClientPlayPacketListener> {
	public PlayerActionResponseS2CPacket(PacketByteBuf buf) {
		this(buf.readVarInt());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.sequence);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerActionResponse(this);
	}
}
