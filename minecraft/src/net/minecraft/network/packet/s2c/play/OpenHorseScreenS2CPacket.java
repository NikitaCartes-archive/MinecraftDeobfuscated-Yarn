package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class OpenHorseScreenS2CPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private int slotCount;
	private int horseId;

	public OpenHorseScreenS2CPacket() {
	}

	public OpenHorseScreenS2CPacket(int syncId, int slotCount, int horseId) {
		this.syncId = syncId;
		this.slotCount = slotCount;
		this.horseId = horseId;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onOpenHorseScreen(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readUnsignedByte();
		this.slotCount = buf.readVarInt();
		this.horseId = buf.readInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.syncId);
		buf.writeVarInt(this.slotCount);
		buf.writeInt(this.horseId);
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
