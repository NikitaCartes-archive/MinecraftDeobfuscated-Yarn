package net.minecraft.network.packet.s2c.config;

import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.packet.Packet;

public record ReadyS2CPacket() implements Packet<ClientConfigurationPacketListener> {
	public ReadyS2CPacket(PacketByteBuf buf) {
		this();
	}

	@Override
	public void write(PacketByteBuf buf) {
	}

	public void apply(ClientConfigurationPacketListener clientConfigurationPacketListener) {
		clientConfigurationPacketListener.onReady(this);
	}

	@Override
	public NetworkState getNewNetworkState() {
		return NetworkState.PLAY;
	}
}
