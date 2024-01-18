package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class WorldTimeUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, WorldTimeUpdateS2CPacket> CODEC = Packet.createCodec(
		WorldTimeUpdateS2CPacket::write, WorldTimeUpdateS2CPacket::new
	);
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

	private WorldTimeUpdateS2CPacket(PacketByteBuf buf) {
		this.time = buf.readLong();
		this.timeOfDay = buf.readLong();
	}

	private void write(PacketByteBuf buf) {
		buf.writeLong(this.time);
		buf.writeLong(this.timeOfDay);
	}

	@Override
	public PacketType<WorldTimeUpdateS2CPacket> getPacketId() {
		return PlayPackets.SET_TIME;
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
