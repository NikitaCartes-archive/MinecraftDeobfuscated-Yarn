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
import net.minecraft.util.math.Vec3d;

public record PlayerPositionLookS2CPacket(int teleportId, Vec3d position, Vec3d deltaMovement, float yaw, float pitch, Set<PositionFlag> flags)
	implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, PlayerPositionLookS2CPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT,
		PlayerPositionLookS2CPacket::teleportId,
		Vec3d.PACKET_CODEC,
		PlayerPositionLookS2CPacket::position,
		Vec3d.PACKET_CODEC,
		PlayerPositionLookS2CPacket::deltaMovement,
		PacketCodecs.DEGREES,
		PlayerPositionLookS2CPacket::yaw,
		PacketCodecs.DEGREES,
		PlayerPositionLookS2CPacket::pitch,
		PositionFlag.PACKET_CODEC,
		PlayerPositionLookS2CPacket::flags,
		PlayerPositionLookS2CPacket::new
	);

	public static PlayerPositionLookS2CPacket of(int teleportId, PlayerPosition pos, Set<PositionFlag> flags) {
		return new PlayerPositionLookS2CPacket(teleportId, pos.position(), pos.deltaMovement(), pos.yaw(), pos.pitch(), flags);
	}

	@Override
	public PacketType<PlayerPositionLookS2CPacket> getPacketId() {
		return PlayPackets.PLAYER_POSITION;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerPositionLook(this);
	}
}
