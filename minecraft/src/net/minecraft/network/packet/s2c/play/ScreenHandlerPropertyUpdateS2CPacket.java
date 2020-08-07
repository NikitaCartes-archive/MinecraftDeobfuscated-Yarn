package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ScreenHandlerPropertyUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private int propertyId;
	private int value;

	public ScreenHandlerPropertyUpdateS2CPacket() {
	}

	public ScreenHandlerPropertyUpdateS2CPacket(int syncId, int propertyId, int value) {
		this.syncId = syncId;
		this.propertyId = propertyId;
		this.value = value;
	}

	public void method_11447(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScreenHandlerPropertyUpdate(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readUnsignedByte();
		this.propertyId = buf.readShort();
		this.value = buf.readShort();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.syncId);
		buf.writeShort(this.propertyId);
		buf.writeShort(this.value);
	}

	@Environment(EnvType.CLIENT)
	public int getSyncId() {
		return this.syncId;
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
