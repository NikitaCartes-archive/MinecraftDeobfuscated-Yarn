package net.minecraft.network.packet.s2c.play;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record EntityTrackerUpdateS2CPacket(int id, List<DataTracker.SerializedEntry<?>> trackedValues) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, EntityTrackerUpdateS2CPacket> CODEC = Packet.createCodec(
		EntityTrackerUpdateS2CPacket::write, EntityTrackerUpdateS2CPacket::new
	);
	public static final int MARKER_ID = 255;

	private EntityTrackerUpdateS2CPacket(RegistryByteBuf buf) {
		this(buf.readVarInt(), read(buf));
	}

	private static void write(List<DataTracker.SerializedEntry<?>> trackedValues, RegistryByteBuf buf) {
		for (DataTracker.SerializedEntry<?> serializedEntry : trackedValues) {
			serializedEntry.write(buf);
		}

		buf.writeByte(255);
	}

	private static List<DataTracker.SerializedEntry<?>> read(RegistryByteBuf buf) {
		List<DataTracker.SerializedEntry<?>> list = new ArrayList();

		int i;
		while ((i = buf.readUnsignedByte()) != 255) {
			list.add(DataTracker.SerializedEntry.fromBuf(buf, i));
		}

		return list;
	}

	private void write(RegistryByteBuf buf) {
		buf.writeVarInt(this.id);
		write(this.trackedValues, buf);
	}

	@Override
	public PacketType<EntityTrackerUpdateS2CPacket> getPacketId() {
		return PlayPackets.SET_ENTITY_DATA;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityTrackerUpdate(this);
	}
}
