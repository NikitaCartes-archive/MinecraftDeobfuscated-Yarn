package net.minecraft.network.packet.s2c.config;

import java.lang.runtime.ObjectMethods;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.packet.Packet;

public final class ReadyS2CPacket extends Record implements Packet<ClientConfigurationPacketListener> {
	public ReadyS2CPacket(PacketByteBuf buf) {
		this();
	}

	public ReadyS2CPacket() {
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

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",ReadyS2CPacket,"">(this);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",ReadyS2CPacket,"">(this);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",ReadyS2CPacket,"">(this, object);
	}
}
