package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record PlayerRotationS2CPaket(float yRot, float xRot) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, PlayerRotationS2CPaket> CODEC = PacketCodec.tuple(
		PacketCodecs.FLOAT, PlayerRotationS2CPaket::yRot, PacketCodecs.FLOAT, PlayerRotationS2CPaket::xRot, PlayerRotationS2CPaket::new
	);

	@Override
	public PacketType<PlayerRotationS2CPaket> getPacketId() {
		return PlayPackets.PLAYER_ROTATION;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerRotation(this);
	}
}
