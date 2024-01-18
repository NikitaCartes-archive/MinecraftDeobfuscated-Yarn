package net.minecraft.network.packet.c2s.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public class KeepAliveC2SPacket implements Packet<ServerCommonPacketListener> {
	public static final PacketCodec<PacketByteBuf, KeepAliveC2SPacket> CODEC = Packet.createCodec(KeepAliveC2SPacket::write, KeepAliveC2SPacket::new);
	private final long id;

	public KeepAliveC2SPacket(long id) {
		this.id = id;
	}

	private KeepAliveC2SPacket(PacketByteBuf buf) {
		this.id = buf.readLong();
	}

	private void write(PacketByteBuf buf) {
		buf.writeLong(this.id);
	}

	@Override
	public PacketType<KeepAliveC2SPacket> getPacketId() {
		return CommonPackets.KEEP_ALIVE_C2S;
	}

	public void apply(ServerCommonPacketListener serverCommonPacketListener) {
		serverCommonPacketListener.onKeepAlive(this);
	}

	public long getId() {
		return this.id;
	}
}
