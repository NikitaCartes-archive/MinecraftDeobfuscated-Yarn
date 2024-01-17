package net.minecraft;

import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.network.packet.PingPackets;
import net.minecraft.network.packet.StatusPackets;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;

public class class_9103 {
	public static final class_9127<ServerQueryPacketListener> field_48263 = class_9147.method_56451(
		NetworkState.STATUS,
		arg -> arg.method_56454(StatusPackets.STATUS_REQUEST, QueryRequestC2SPacket.CODEC).method_56454(PingPackets.PING_REQUEST, QueryPingC2SPacket.CODEC)
	);
	public static final class_9127<ClientQueryPacketListener> field_48264 = class_9147.method_56455(
		NetworkState.STATUS,
		arg -> arg.method_56454(StatusPackets.STATUS_RESPONSE, QueryResponseS2CPacket.CODEC).method_56454(PingPackets.PONG_RESPONSE, PingResultS2CPacket.CODEC)
	);
}
