package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class PickFromInventoryC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int slot;

	public PickFromInventoryC2SPacket(int slot) {
		this.slot = slot;
	}

	public PickFromInventoryC2SPacket(PacketByteBuf buf) {
		this.slot = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.slot);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPickFromInventory(this);
	}

	public int getSlot() {
		return this.slot;
	}
}
