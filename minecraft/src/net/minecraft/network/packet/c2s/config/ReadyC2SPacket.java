package net.minecraft.network.packet.c2s.config;

import java.lang.runtime.ObjectMethods;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerConfigurationPacketListener;
import net.minecraft.network.packet.Packet;

public final class ReadyC2SPacket extends Record implements Packet<ServerConfigurationPacketListener> {
	public ReadyC2SPacket(PacketByteBuf buf) {
		this();
	}

	public ReadyC2SPacket() {
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

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",ReadyC2SPacket,"">(this);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",ReadyC2SPacket,"">(this);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",ReadyC2SPacket,"">(this, object);
	}
}
