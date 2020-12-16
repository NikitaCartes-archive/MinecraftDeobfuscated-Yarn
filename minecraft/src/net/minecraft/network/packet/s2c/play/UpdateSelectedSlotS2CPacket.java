package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class UpdateSelectedSlotS2CPacket implements Packet<ClientPlayPacketListener> {
	private int selectedSlot;

	public UpdateSelectedSlotS2CPacket() {
	}

	public UpdateSelectedSlotS2CPacket(int slot) {
		this.selectedSlot = slot;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.selectedSlot = buf.readByte();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.selectedSlot);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onHeldItemChange(this);
	}

	@Environment(EnvType.CLIENT)
	public int getSlot() {
		return this.selectedSlot;
	}
}
