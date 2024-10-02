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

public record EntityPositionS2CPacket(int entityId, PlayerPosition change, Set<PositionFlag> relatives, boolean onGround)
	implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, EntityPositionS2CPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT,
		EntityPositionS2CPacket::entityId,
		PlayerPosition.PACKET_CODEC,
		EntityPositionS2CPacket::change,
		PositionFlag.PACKET_CODEC,
		EntityPositionS2CPacket::relatives,
		PacketCodecs.BOOL,
		EntityPositionS2CPacket::onGround,
		EntityPositionS2CPacket::new
	);

	public static EntityPositionS2CPacket create(int entityId, PlayerPosition change, Set<PositionFlag> relatives, boolean onGround) {
		return new EntityPositionS2CPacket(entityId, change, relatives, onGround);
	}

	@Override
	public PacketType<EntityPositionS2CPacket> getPacketId() {
		return PlayPackets.TELEPORT_ENTITY;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityPosition(this);
	}
}
