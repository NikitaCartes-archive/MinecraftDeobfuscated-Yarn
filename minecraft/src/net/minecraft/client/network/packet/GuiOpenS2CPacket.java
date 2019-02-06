package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class GuiOpenS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private int slotCount;
	private int entityHorseId;

	public GuiOpenS2CPacket() {
	}

	public GuiOpenS2CPacket(int i, int j, int k) {
		this.id = i;
		this.slotCount = j;
		this.entityHorseId = k;
	}

	public void method_11437(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11089(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readUnsignedByte();
		this.slotCount = packetByteBuf.readVarInt();
		this.entityHorseId = packetByteBuf.readInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.id);
		packetByteBuf.writeVarInt(this.slotCount);
		packetByteBuf.writeInt(this.entityHorseId);
	}

	@Environment(EnvType.CLIENT)
	public int getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public int getSlotCount() {
		return this.slotCount;
	}

	@Environment(EnvType.CLIENT)
	public int getHorseId() {
		return this.entityHorseId;
	}
}
