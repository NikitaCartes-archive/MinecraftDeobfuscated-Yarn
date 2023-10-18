package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

public record SlotChangedStateC2SPacket(int slotId, int screenHandlerId, boolean newState) implements Packet<ServerPlayPacketListener> {
	public SlotChangedStateC2SPacket(PacketByteBuf buf) {
		this(buf.readVarInt(), buf.readVarInt(), buf.readBoolean());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.slotId);
		buf.writeVarInt(this.screenHandlerId);
		buf.writeBoolean(this.newState);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onSlotChangedState(this);
	}
}
