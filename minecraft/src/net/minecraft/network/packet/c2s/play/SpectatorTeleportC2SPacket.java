package net.minecraft.network.packet.c2s.play;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.server.world.ServerWorld;

public class SpectatorTeleportC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, SpectatorTeleportC2SPacket> CODEC = Packet.createCodec(
		SpectatorTeleportC2SPacket::write, SpectatorTeleportC2SPacket::new
	);
	private final UUID targetUuid;

	public SpectatorTeleportC2SPacket(UUID targetUuid) {
		this.targetUuid = targetUuid;
	}

	private SpectatorTeleportC2SPacket(PacketByteBuf buf) {
		this.targetUuid = buf.readUuid();
	}

	private void write(PacketByteBuf buf) {
		buf.writeUuid(this.targetUuid);
	}

	@Override
	public PacketType<SpectatorTeleportC2SPacket> getPacketId() {
		return PlayPackets.TELEPORT_TO_ENTITY;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onSpectatorTeleport(this);
	}

	@Nullable
	public Entity getTarget(ServerWorld world) {
		return world.getEntity(this.targetUuid);
	}
}
