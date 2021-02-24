package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class CloseHandledScreenC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int syncId;

	@Environment(EnvType.CLIENT)
	public CloseHandledScreenC2SPacket(int syncId) {
		this.syncId = syncId;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCloseHandledScreen(this);
	}

	public CloseHandledScreenC2SPacket(PacketByteBuf packetByteBuf) {
		this.syncId = packetByteBuf.readByte();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByte(this.syncId);
	}
}
