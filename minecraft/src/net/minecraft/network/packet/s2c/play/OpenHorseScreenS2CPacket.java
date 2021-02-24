package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class OpenHorseScreenS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int syncId;
	private final int slotCount;
	private final int horseId;

	public OpenHorseScreenS2CPacket(int syncId, int slotCount, int horseId) {
		this.syncId = syncId;
		this.slotCount = slotCount;
		this.horseId = horseId;
	}

	public OpenHorseScreenS2CPacket(PacketByteBuf packetByteBuf) {
		this.syncId = packetByteBuf.readUnsignedByte();
		this.slotCount = packetByteBuf.readVarInt();
		this.horseId = packetByteBuf.readInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByte(this.syncId);
		buf.writeVarInt(this.slotCount);
		buf.writeInt(this.horseId);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onOpenHorseScreen(this);
	}

	@Environment(EnvType.CLIENT)
	public int getSyncId() {
		return this.syncId;
	}

	@Environment(EnvType.CLIENT)
	public int getSlotCount() {
		return this.slotCount;
	}

	@Environment(EnvType.CLIENT)
	public int getHorseId() {
		return this.horseId;
	}
}
