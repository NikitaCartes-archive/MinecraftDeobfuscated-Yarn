package net.minecraft.network.packet.c2s.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerCommonPacketListener;
import net.minecraft.network.packet.Packet;

public class KeepAliveC2SPacket implements Packet<ServerCommonPacketListener> {
	private final long id;

	public KeepAliveC2SPacket(long id) {
		this.id = id;
	}

	public void apply(ServerCommonPacketListener serverCommonPacketListener) {
		serverCommonPacketListener.onKeepAlive(this);
	}

	public KeepAliveC2SPacket(PacketByteBuf buf) {
		this.id = buf.readLong();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeLong(this.id);
	}

	public long getId() {
		return this.id;
	}
}
