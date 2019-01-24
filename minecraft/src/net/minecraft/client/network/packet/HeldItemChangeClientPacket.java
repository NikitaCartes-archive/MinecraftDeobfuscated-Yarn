package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class HeldItemChangeClientPacket implements Packet<ClientPlayPacketListener> {
	private int slot;

	public HeldItemChangeClientPacket() {
	}

	public HeldItemChangeClientPacket(int i) {
		this.slot = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.slot = packetByteBuf.readByte();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.slot);
	}

	public void method_11802(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onHeldItemChange(this);
	}

	@Environment(EnvType.CLIENT)
	public int getSlot() {
		return this.slot;
	}
}
