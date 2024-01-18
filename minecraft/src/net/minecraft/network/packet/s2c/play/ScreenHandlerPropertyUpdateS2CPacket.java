package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class ScreenHandlerPropertyUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ScreenHandlerPropertyUpdateS2CPacket> CODEC = Packet.createCodec(
		ScreenHandlerPropertyUpdateS2CPacket::write, ScreenHandlerPropertyUpdateS2CPacket::new
	);
	private final int syncId;
	private final int propertyId;
	private final int value;

	public ScreenHandlerPropertyUpdateS2CPacket(int syncId, int propertyId, int value) {
		this.syncId = syncId;
		this.propertyId = propertyId;
		this.value = value;
	}

	private ScreenHandlerPropertyUpdateS2CPacket(PacketByteBuf buf) {
		this.syncId = buf.readUnsignedByte();
		this.propertyId = buf.readShort();
		this.value = buf.readShort();
	}

	private void write(PacketByteBuf buf) {
		buf.writeByte(this.syncId);
		buf.writeShort(this.propertyId);
		buf.writeShort(this.value);
	}

	@Override
	public PacketType<ScreenHandlerPropertyUpdateS2CPacket> getPacketId() {
		return PlayPackets.CONTAINER_SET_DATA;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScreenHandlerPropertyUpdate(this);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public int getPropertyId() {
		return this.propertyId;
	}

	public int getValue() {
		return this.value;
	}
}
