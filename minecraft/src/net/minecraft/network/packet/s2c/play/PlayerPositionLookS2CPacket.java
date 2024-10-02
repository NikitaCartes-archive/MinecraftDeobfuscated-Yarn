package net.minecraft.network.packet.s2c.play;

import java.util.Set;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record PlayerPositionLookS2CPacket(int teleportId, PlayerPosition change, Set<PositionFlag> relatives) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, PlayerPositionLookS2CPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT,
		PlayerPositionLookS2CPacket::teleportId,
		PlayerPosition.PACKET_CODEC,
		PlayerPositionLookS2CPacket::change,
		PositionFlag.PACKET_CODEC,
		PlayerPositionLookS2CPacket::relatives,
		PlayerPositionLookS2CPacket::new
	);

	public static PlayerPositionLookS2CPacket of(int teleportId, PlayerPosition pos, Set<PositionFlag> flags) {
		return new PlayerPositionLookS2CPacket(teleportId, pos, flags);
	}

	@Override
	public PacketType<PlayerPositionLookS2CPacket> getPacketId() {
		return PlayPackets.PLAYER_POSITION;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerPositionLook(this);
	}
}
