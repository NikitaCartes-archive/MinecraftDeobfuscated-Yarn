package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class UpdateSelectedSlotServerPacket implements Packet<ServerPlayPacketListener> {
	private int selectedSlot;

	public UpdateSelectedSlotServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public UpdateSelectedSlotServerPacket(int i) {
		this.selectedSlot = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.selectedSlot = packetByteBuf.readShort();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeShort(this.selectedSlot);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateSelectedSlot(this);
	}

	public int getSelectedSlot() {
		return this.selectedSlot;
	}
}
