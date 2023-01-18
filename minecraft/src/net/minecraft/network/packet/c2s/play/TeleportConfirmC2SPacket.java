package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class TeleportConfirmC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int teleportId;

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
