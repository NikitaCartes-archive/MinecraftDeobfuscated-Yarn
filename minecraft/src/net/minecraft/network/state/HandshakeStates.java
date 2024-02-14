package net.minecraft.network.state;

import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkState;
import net.minecraft.network.NetworkStateBuilder;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.HandshakePackets;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;

public class HandshakeStates {
	public static final NetworkState<ServerHandshakePacketListener> C2S = NetworkStateBuilder.c2s(
		NetworkPhase.HANDSHAKING, builder -> builder.add(HandshakePackets.INTENTION, HandshakeC2SPacket.CODEC)
	);
}
