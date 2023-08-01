package net.minecraft.network.packet.s2c.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.Packet;

public class KeepAliveS2CPacket implements Packet<ClientCommonPacketListener> {
	private final long id;

	public KeepAliveS2CPacket(long id) {
		this.id = id;
	}

	public KeepAliveS2CPacket(PacketByteBuf buf) {
		this.id = buf.readLong();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeLong(this.id);
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onKeepAlive(this);
	}

	public long getId() {
		return this.id;
	}
}
