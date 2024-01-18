package net.minecraft.network.packet.s2c.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public class KeepAliveS2CPacket implements Packet<ClientCommonPacketListener> {
	public static final PacketCodec<PacketByteBuf, KeepAliveS2CPacket> CODEC = Packet.createCodec(KeepAliveS2CPacket::write, KeepAliveS2CPacket::new);
	private final long id;

	public KeepAliveS2CPacket(long id) {
		this.id = id;
	}

	private KeepAliveS2CPacket(PacketByteBuf buf) {
		this.id = buf.readLong();
	}

	private void write(PacketByteBuf buf) {
		buf.writeLong(this.id);
	}

	@Override
	public PacketType<KeepAliveS2CPacket> getPacketId() {
		return CommonPackets.KEEP_ALIVE_S2C;
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onKeepAlive(this);
	}

	public long getId() {
		return this.id;
	}
}
