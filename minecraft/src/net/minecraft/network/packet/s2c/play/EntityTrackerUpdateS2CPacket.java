package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityTrackerUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private List<DataTracker.Entry<?>> trackedValues;

	public EntityTrackerUpdateS2CPacket() {
	}

	public EntityTrackerUpdateS2CPacket(int id, DataTracker tracker, boolean forceUpdateAll) {
		this.id = id;
		if (forceUpdateAll) {
			this.trackedValues = tracker.getAllEntries();
			tracker.clearDirty();
		} else {
			this.trackedValues = tracker.getDirtyEntries();
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.trackedValues = DataTracker.deserializePacket(buf);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		DataTracker.entriesToPacket(this.trackedValues, buf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityTrackerUpdate(this);
	}

	@Environment(EnvType.CLIENT)
	public List<DataTracker.Entry<?>> getTrackedValues() {
		return this.trackedValues;
	}

	@Environment(EnvType.CLIENT)
	public int id() {
		return this.id;
	}
}
