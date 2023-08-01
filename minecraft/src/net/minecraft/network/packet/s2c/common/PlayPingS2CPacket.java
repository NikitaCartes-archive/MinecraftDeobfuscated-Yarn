package net.minecraft.network.packet.s2c.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.Packet;

/**
 * A packet sent by the server; the client will reply with a pong packet on the
 * first tick after it receives this packet, with the same {@link #parameter}.
 * 
 * @see net.minecraft.network.packet.c2s.common.PlayPongC2SPacket
 * @see net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket
 * @see net.minecraft.network.packet.s2c.query.QueryPongS2CPacket
 */
public class PlayPingS2CPacket implements Packet<ClientCommonPacketListener> {
	/**
	 * The parameter of this ping packet.
	 * 
	 * <p>If this number represents a tick, this could measure the network delay in
	 * ticks. It is possible to be a tick number given the reply packet is sent on
	 * the client on the main thread's tick, and the number is sent as a regular int
	 * than a varint.
	 */
	private final int parameter;

	public PlayPingS2CPacket(int parameter) {
		this.parameter = parameter;
	}

	public PlayPingS2CPacket(PacketByteBuf buf) {
		this.parameter = buf.readInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.parameter);
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onPlayPing(this);
	}

	public int getParameter() {
		return this.parameter;
	}
}
