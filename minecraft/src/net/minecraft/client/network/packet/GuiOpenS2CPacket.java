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

	public GuiOpenS2CPacket(int id, int slotCount, int entityHorseId) {
		this.id = id;
		this.slotCount = slotCount;
		this.entityHorseId = entityHorseId;
	}

	public void method_11437(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGuiOpen(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readUnsignedByte();
		this.slotCount = buf.readVarInt();
		this.entityHorseId = buf.readInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.id);
		buf.writeVarInt(this.slotCount);
		buf.writeInt(this.entityHorseId);
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
