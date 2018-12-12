package net.minecraft.server.network.packet;

import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.PacketByteBuf;

public class SpectatorTeleportServerPacket implements Packet<ServerPlayPacketListener> {
	private UUID targetUuid;

	public SpectatorTeleportServerPacket() {
	}

	public SpectatorTeleportServerPacket(UUID uUID) {
		this.targetUuid = uUID;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.targetUuid = packetByteBuf.readUuid();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeUuid(this.targetUuid);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onSpectatorTeleport(this);
	}

	@Nullable
	public Entity getTarget(ServerWorld serverWorld) {
		return serverWorld.getEntityByUuid(this.targetUuid);
	}
}
