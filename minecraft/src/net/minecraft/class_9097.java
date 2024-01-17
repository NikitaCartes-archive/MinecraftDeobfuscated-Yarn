package net.minecraft;

import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.HandshakePackets;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;

public class class_9097 {
	public static final class_9127<ServerHandshakePacketListener> field_48231 = class_9147.method_56451(
		NetworkState.HANDSHAKING, arg -> arg.method_56454(HandshakePackets.INTENTION, HandshakeC2SPacket.CODEC)
	);
}
