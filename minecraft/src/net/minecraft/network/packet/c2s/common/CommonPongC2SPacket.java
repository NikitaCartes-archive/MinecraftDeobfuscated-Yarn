package net.minecraft.network.packet.c2s.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

/**
 * This is a packet that is sent by the client during tick after receiving a
 * play ping packet from the server, passing the {@link #parameter} back to the
 * server.
 * 
 * @see net.minecraft.network.packet.s2c.common.CommonPingS2CPacket
 * @see net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket
 * @see net.minecraft.network.packet.c2s.query.QueryPingC2SPacket
 */
public class CommonPongC2SPacket implements Packet<ServerCommonPacketListener> {
	public static final PacketCodec<PacketByteBuf, CommonPongC2SPacket> CODEC = Packet.createCodec(CommonPongC2SPacket::write, CommonPongC2SPacket::new);
	private final int parameter;

	public CommonPongC2SPacket(int parameter) {
		this.parameter = parameter;
	}

	private CommonPongC2SPacket(PacketByteBuf buf) {
		this.parameter = buf.readInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeInt(this.parameter);
	}

	@Override
	public PacketType<CommonPongC2SPacket> getPacketId() {
		return CommonPackets.PONG;
	}

	public void apply(ServerCommonPacketListener serverCommonPacketListener) {
		serverCommonPacketListener.onPong(this);
	}

	public int getParameter() {
		return this.parameter;
	}
}
