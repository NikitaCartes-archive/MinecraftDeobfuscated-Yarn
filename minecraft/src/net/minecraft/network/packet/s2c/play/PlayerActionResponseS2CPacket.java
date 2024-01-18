package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record PlayerActionResponseS2CPacket(int sequence) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, PlayerActionResponseS2CPacket> CODEC = Packet.createCodec(
		PlayerActionResponseS2CPacket::write, PlayerActionResponseS2CPacket::new
	);

	private PlayerActionResponseS2CPacket(PacketByteBuf buf) {
		this(buf.readVarInt());
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.sequence);
	}

	@Override
	public PacketType<PlayerActionResponseS2CPacket> getPacketId() {
		return PlayPackets.BLOCK_CHANGED_ACK;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerActionResponse(this);
	}
}
