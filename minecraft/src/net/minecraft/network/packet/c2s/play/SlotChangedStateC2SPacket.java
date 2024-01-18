package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record SlotChangedStateC2SPacket(int slotId, int screenHandlerId, boolean newState) implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, SlotChangedStateC2SPacket> CODEC = Packet.createCodec(
		SlotChangedStateC2SPacket::write, SlotChangedStateC2SPacket::new
	);

	private SlotChangedStateC2SPacket(PacketByteBuf buf) {
		this(buf.readVarInt(), buf.readVarInt(), buf.readBoolean());
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.slotId);
		buf.writeVarInt(this.screenHandlerId);
		buf.writeBoolean(this.newState);
	}

	@Override
	public PacketType<SlotChangedStateC2SPacket> getPacketId() {
		return PlayPackets.CONTAINER_SLOT_STATE_CHANGED;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onSlotChangedState(this);
	}
}
