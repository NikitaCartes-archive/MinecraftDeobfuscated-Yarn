package net.minecraft.network.packet.s2c.play;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.World;

public record MoveMinecartAlongTrackS2CPacket(int entityId, List<ExperimentalMinecartController.Step> lerpSteps) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, MoveMinecartAlongTrackS2CPacket> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT,
		MoveMinecartAlongTrackS2CPacket::entityId,
		ExperimentalMinecartController.Step.PACKET_CODEC.collect(PacketCodecs.toList()),
		MoveMinecartAlongTrackS2CPacket::lerpSteps,
		MoveMinecartAlongTrackS2CPacket::new
	);

	@Override
	public PacketType<MoveMinecartAlongTrackS2CPacket> getPacketId() {
		return PlayPackets.MOVE_MINECART_ALONG_TRACK;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onMoveMinecartAlongTrack(this);
	}

	@Nullable
	public Entity getEntity(World world) {
		return world.getEntityById(this.entityId);
	}
}
