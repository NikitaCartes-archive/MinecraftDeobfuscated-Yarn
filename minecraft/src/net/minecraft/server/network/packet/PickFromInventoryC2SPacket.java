package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class PickFromInventoryC2SPacket implements Packet<ServerPlayPacketListener> {
	private int slot;

	public PickFromInventoryC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public PickFromInventoryC2SPacket(int i) {
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
		serverPlayPacketListener.method_12084(this);
	}

	public int getSlot() {
		return this.slot;
	}
}
