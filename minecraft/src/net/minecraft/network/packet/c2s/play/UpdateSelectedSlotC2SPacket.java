package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class UpdateSelectedSlotC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int selectedSlot;

	@Environment(EnvType.CLIENT)
	public UpdateSelectedSlotC2SPacket(int selectedSlot) {
		this.selectedSlot = selectedSlot;
	}

	public UpdateSelectedSlotC2SPacket(PacketByteBuf packetByteBuf) {
		this.selectedSlot = packetByteBuf.readShort();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeShort(this.selectedSlot);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateSelectedSlot(this);
	}

	public int getSelectedSlot() {
		return this.selectedSlot;
	}
}
