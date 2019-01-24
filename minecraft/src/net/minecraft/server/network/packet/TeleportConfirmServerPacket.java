package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class TeleportConfirmServerPacket implements Packet<ServerPlayPacketListener> {
	private int teleportId;

	public TeleportConfirmServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public TeleportConfirmServerPacket(int i) {
		this.teleportId = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.teleportId = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.teleportId);
	}

	public void method_12085(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onTeleportConfirm(this);
	}

	public int getTeleportId() {
		return this.teleportId;
	}
}
