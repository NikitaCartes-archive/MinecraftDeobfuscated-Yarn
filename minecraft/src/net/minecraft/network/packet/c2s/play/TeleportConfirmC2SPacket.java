package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class TeleportConfirmC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int teleportId;

	@Environment(EnvType.CLIENT)
	public TeleportConfirmC2SPacket(int teleportId) {
		this.teleportId = teleportId;
	}

	public TeleportConfirmC2SPacket(PacketByteBuf buf) {
		this.teleportId = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.teleportId);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onTeleportConfirm(this);
	}

	public int getTeleportId() {
		return this.teleportId;
	}
}
