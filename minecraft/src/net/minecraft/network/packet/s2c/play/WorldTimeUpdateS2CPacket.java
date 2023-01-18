package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class WorldTimeUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final long time;
	private final long timeOfDay;

	public WorldTimeUpdateS2CPacket(long time, long timeOfDay, boolean doDaylightCycle) {
		this.time = time;
		long l = timeOfDay;
		if (!doDaylightCycle) {
			l = -timeOfDay;
			if (l == 0L) {
				l = -1L;
			}
		}

		this.timeOfDay = l;
	}

	public WorldTimeUpdateS2CPacket(PacketByteBuf buf) {
		this.time = buf.readLong();
		this.timeOfDay = buf.readLong();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeLong(this.time);
		buf.writeLong(this.timeOfDay);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onWorldTimeUpdate(this);
	}

	public long getTime() {
		return this.time;
	}

	public long getTimeOfDay() {
		return this.timeOfDay;
	}
}
