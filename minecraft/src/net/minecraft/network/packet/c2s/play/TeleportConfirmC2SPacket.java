package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class TeleportConfirmC2SPacket implements Packet<ServerPlayPacketListener> {
	private int teleportId;

	public TeleportConfirmC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public TeleportConfirmC2SPacket(int teleportId) {
		this.teleportId = teleportId;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.teleportId = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.teleportId);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onTeleportConfirm(this);
	}

	public int getTeleportId() {
		return this.teleportId;
	}
}
