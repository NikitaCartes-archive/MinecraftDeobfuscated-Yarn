package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

/**
 * This is a packet that is sent by the client during tick after receiving a
 * play ping packet from the server, passing the {@link #parameter} back to the
 * server.
 * 
 * @see net.minecraft.network.packet.s2c.play.PlayPingS2CPacket
 * @see net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket
 * @see net.minecraft.network.packet.c2s.query.QueryPingC2SPacket
 */
public class PlayPongC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int parameter;

	public PlayPongC2SPacket(int parameter) {
		this.parameter = parameter;
	}

	public PlayPongC2SPacket(PacketByteBuf buf) {
		this.parameter = buf.readInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.parameter);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPong(this);
	}

	public int getParameter() {
		return this.parameter;
	}
}
