package net.minecraft.network.packet.c2s.config;

import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerConfigurationPacketListener;
import net.minecraft.network.packet.Packet;

public record ReadyC2SPacket() implements Packet<ServerConfigurationPacketListener> {
	public ReadyC2SPacket(PacketByteBuf buf) {
		this();
	}

	@Override
	public void write(PacketByteBuf buf) {
	}

	public void apply(ServerConfigurationPacketListener serverConfigurationPacketListener) {
		serverConfigurationPacketListener.onReady(this);
	}

	@Override
	public NetworkState getNewNetworkState() {
		return NetworkState.PLAY;
	}
}
