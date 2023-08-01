package net.minecraft.network.packet.c2s.login;

import java.lang.runtime.ObjectMethods;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.Packet;

public final class EnterConfigurationC2SPacket extends Record implements Packet<ServerLoginPacketListener> {
	public EnterConfigurationC2SPacket(PacketByteBuf buf) {
		this();
	}

	public EnterConfigurationC2SPacket() {
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

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",EnterConfigurationC2SPacket,"">(this);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",EnterConfigurationC2SPacket,"">(this);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",EnterConfigurationC2SPacket,"">(this, object);
	}
}
