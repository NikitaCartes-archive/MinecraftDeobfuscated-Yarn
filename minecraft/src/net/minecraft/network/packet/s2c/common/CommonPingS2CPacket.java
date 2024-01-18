package net.minecraft.network.packet.s2c.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

/**
 * A packet sent by the server; the client will reply with a pong packet on the
 * first tick after it receives this packet, with the same {@link #parameter}.
 * 
 * @see net.minecraft.network.packet.c2s.common.CommonPongC2SPacket
 * @see net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket
 */
public class CommonPingS2CPacket implements Packet<ClientCommonPacketListener> {
	public static final PacketCodec<PacketByteBuf, CommonPingS2CPacket> CODEC = Packet.createCodec(CommonPingS2CPacket::write, CommonPingS2CPacket::new);
	/**
	 * The parameter of this ping packet.
	 * 
	 * <p>If this number represents a tick, this could measure the network delay in
	 * ticks. It is possible to be a tick number given the reply packet is sent on
	 * the client on the main thread's tick, and the number is sent as a regular int
	 * than a varint.
	 */
	private final int parameter;

	public CommonPingS2CPacket(int parameter) {
		this.parameter = parameter;
	}

	private CommonPingS2CPacket(PacketByteBuf buf) {
		this.parameter = buf.readInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeInt(this.parameter);
	}

	@Override
	public PacketType<CommonPingS2CPacket> getPacketId() {
		return CommonPackets.PING;
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onPing(this);
	}

	public int getParameter() {
		return this.parameter;
	}
}
