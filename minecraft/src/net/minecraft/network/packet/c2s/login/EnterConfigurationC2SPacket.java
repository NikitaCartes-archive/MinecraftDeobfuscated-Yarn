package net.minecraft.network.packet.c2s.login;

import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.Packet;

public record EnterConfigurationC2SPacket() implements Packet<ServerLoginPacketListener> {
	public EnterConfigurationC2SPacket(PacketByteBuf buf) {
		this();
	}

	@Override
	public void write(PacketByteBuf buf) {
	}

	public void apply(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.onEnterConfiguration(this);
	}

	@Override
	public NetworkState getNewNetworkState() {
		return NetworkState.CONFIGURATION;
	}
}
