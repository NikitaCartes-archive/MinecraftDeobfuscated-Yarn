package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class WorldTimeUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private long time;
	private long timeOfDay;

	public WorldTimeUpdateS2CPacket() {
	}

	public WorldTimeUpdateS2CPacket(long l, long m, boolean bl) {
		this.time = l;
		this.timeOfDay = m;
		if (!bl) {
			this.timeOfDay = -this.timeOfDay;
			if (this.timeOfDay == 0L) {
				this.timeOfDay = -1L;
			}
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.time = packetByteBuf.readLong();
		this.timeOfDay = packetByteBuf.readLong();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeLong(this.time);
		packetByteBuf.writeLong(this.timeOfDay);
	}

	public void method_11872(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11079(this);
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
