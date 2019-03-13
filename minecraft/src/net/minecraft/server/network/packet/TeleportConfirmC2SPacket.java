package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class TeleportConfirmC2SPacket implements Packet<ServerPlayPacketListener> {
	private int teleportId;

	public TeleportConfirmC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public TeleportConfirmC2SPacket(int i) {
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
		serverPlayPacketListener.method_12050(this);
	}

	public int getTeleportId() {
		return this.teleportId;
	}
}
