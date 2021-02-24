package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

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

	public WorldTimeUpdateS2CPacket(PacketByteBuf packetByteBuf) {
		this.time = packetByteBuf.readLong();
		this.timeOfDay = packetByteBuf.readLong();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeLong(this.time);
		buf.writeLong(this.timeOfDay);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onWorldTimeUpdate(this);
	}

	@Environment(EnvType.CLIENT)
	public long getTime() {
		return this.time;
	}

	@Environment(EnvType.CLIENT)
	public long getTimeOfDay() {
		return this.timeOfDay;
	}
}
