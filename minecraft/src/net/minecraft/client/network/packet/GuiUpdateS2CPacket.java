package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class GuiUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private int propertyId;
	private int value;

	public GuiUpdateS2CPacket() {
	}

	public GuiUpdateS2CPacket(int window, int propertyId, int value) {
		this.id = window;
		this.propertyId = propertyId;
		this.value = value;
	}

	public void method_11447(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGuiUpdate(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readUnsignedByte();
		this.propertyId = buf.readShort();
		this.value = buf.readShort();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.id);
		buf.writeShort(this.propertyId);
		buf.writeShort(this.value);
	}

	@Environment(EnvType.CLIENT)
	public int getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public int getPropertyId() {
		return this.propertyId;
	}

	@Environment(EnvType.CLIENT)
	public int getValue() {
		return this.value;
	}
}
