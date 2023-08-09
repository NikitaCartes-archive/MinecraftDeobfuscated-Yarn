package net.minecraft.network.packet.s2c.query;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPingResultPacketListener;
import net.minecraft.network.packet.Packet;

public class PingResultS2CPacket implements Packet<ClientPingResultPacketListener> {
	private final long startTime;

	public PingResultS2CPacket(long startTime) {
		this.startTime = startTime;
	}

	public PingResultS2CPacket(PacketByteBuf buf) {
		this.startTime = buf.readLong();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeLong(this.startTime);
	}

	public void apply(ClientPingResultPacketListener clientPingResultPacketListener) {
		clientPingResultPacketListener.onPingResult(this);
	}

	public long getStartTime() {
		return this.startTime;
	}
}
