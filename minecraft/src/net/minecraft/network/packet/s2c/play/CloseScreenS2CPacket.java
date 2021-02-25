package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class CloseScreenS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int syncId;

	public CloseScreenS2CPacket(int syncId) {
		this.syncId = syncId;
	}

	public CloseScreenS2CPacket(PacketByteBuf buf) {
		this.syncId = buf.readUnsignedByte();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByte(this.syncId);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCloseScreen(this);
	}
}
