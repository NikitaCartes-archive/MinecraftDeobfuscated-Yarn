package net.minecraft.network.packet.s2c.play;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityTrackerUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int id;
	@Nullable
	private final List<DataTracker.Entry<?>> trackedValues;

	public EntityTrackerUpdateS2CPacket(int id, DataTracker tracker, boolean forceUpdateAll) {
		this.id = id;
		if (forceUpdateAll) {
			this.trackedValues = tracker.getAllEntries();
			tracker.clearDirty();
		} else {
			this.trackedValues = tracker.getDirtyEntries();
		}
	}

	public EntityTrackerUpdateS2CPacket(PacketByteBuf packetByteBuf) {
		this.id = packetByteBuf.readVarInt();
		this.trackedValues = DataTracker.deserializePacket(packetByteBuf);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.id);
		DataTracker.entriesToPacket(this.trackedValues, buf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityTrackerUpdate(this);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public List<DataTracker.Entry<?>> getTrackedValues() {
		return this.trackedValues;
	}

	@Environment(EnvType.CLIENT)
	public int id() {
		return this.id;
	}
}
