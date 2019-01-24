package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class PickFromInventoryServerPacket implements Packet<ServerPlayPacketListener> {
	private int slot;

	public PickFromInventoryServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public PickFromInventoryServerPacket(int i) {
		this.slot = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.slot = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.slot);
	}

	public void method_12292(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPickFromInventory(this);
	}

	public int getSlot() {
		return this.slot;
	}
}
