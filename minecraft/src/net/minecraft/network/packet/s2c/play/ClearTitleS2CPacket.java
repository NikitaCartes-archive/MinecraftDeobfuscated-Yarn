package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class ClearTitleS2CPacket implements Packet<ClientPlayPacketListener> {
	private final boolean reset;

	public ClearTitleS2CPacket(boolean reset) {
		this.reset = reset;
	}

	public ClearTitleS2CPacket(PacketByteBuf buf) {
		this.reset = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBoolean(this.reset);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTitleClear(this);
	}

	public boolean shouldReset() {
		return this.reset;
	}
}
