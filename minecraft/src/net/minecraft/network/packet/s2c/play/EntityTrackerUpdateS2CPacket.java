package net.minecraft.network.packet.s2c.play;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public record EntityTrackerUpdateS2CPacket(int id, List<DataTracker.SerializedEntry<?>> trackedValues) implements Packet<ClientPlayPacketListener> {
	public static final int MARKER_ID = 255;

	public EntityTrackerUpdateS2CPacket(PacketByteBuf buf) {
		this(buf.readVarInt(), read(buf));
	}

	private static void write(List<DataTracker.SerializedEntry<?>> trackedValues, PacketByteBuf buf) {
		for (DataTracker.SerializedEntry<?> serializedEntry : trackedValues) {
			serializedEntry.write(buf);
		}

		buf.writeByte(255);
	}

	private static List<DataTracker.SerializedEntry<?>> read(PacketByteBuf buf) {
		List<DataTracker.SerializedEntry<?>> list = new ArrayList();

		int i;
		while ((i = buf.readUnsignedByte()) != 255) {
			list.add(DataTracker.SerializedEntry.fromBuf(buf, i));
		}

		return list;
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.id);
		write(this.trackedValues, buf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityTrackerUpdate(this);
	}
}
