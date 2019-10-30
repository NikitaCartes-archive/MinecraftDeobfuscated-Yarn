package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class HeldItemChangeS2CPacket implements Packet<ClientPlayPacketListener> {
	private int slot;

	public HeldItemChangeS2CPacket() {
	}

	public HeldItemChangeS2CPacket(int slot) {
		this.slot = slot;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.slot = buf.readByte();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.slot);
	}

	public void method_11802(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onHeldItemChange(this);
	}

	@Environment(EnvType.CLIENT)
	public int getSlot() {
		return this.slot;
	}
}
