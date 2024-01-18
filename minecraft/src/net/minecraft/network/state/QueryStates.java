package net.minecraft.network.state;

import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkState;
import net.minecraft.network.NetworkStateBuilder;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.network.packet.PingPackets;
import net.minecraft.network.packet.StatusPackets;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;

public class QueryStates {
	public static final NetworkState<ServerQueryPacketListener> C2S = NetworkStateBuilder.c2s(
		NetworkPhase.STATUS,
		builder -> builder.add(StatusPackets.STATUS_REQUEST, QueryRequestC2SPacket.CODEC).add(PingPackets.PING_REQUEST, QueryPingC2SPacket.CODEC)
	);
	public static final NetworkState<ClientQueryPacketListener> S2C = NetworkStateBuilder.s2c(
		NetworkPhase.STATUS,
		builder -> builder.add(StatusPackets.STATUS_RESPONSE, QueryResponseS2CPacket.CODEC).add(PingPackets.PONG_RESPONSE, PingResultS2CPacket.CODEC)
	);
}
