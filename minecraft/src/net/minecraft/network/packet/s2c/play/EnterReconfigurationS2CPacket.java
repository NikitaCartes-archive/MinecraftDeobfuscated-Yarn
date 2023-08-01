package net.minecraft.network.packet.s2c.play;

import java.lang.runtime.ObjectMethods;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public final class EnterReconfigurationS2CPacket extends Record implements Packet<ClientPlayPacketListener> {
	public EnterReconfigurationS2CPacket(PacketByteBuf buf) {
		this();
	}

	public EnterReconfigurationS2CPacket() {
	}

	@Override
	public void write(PacketByteBuf buf) {
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEnterReconfiguration(this);
	}

	@Override
	public NetworkState getNewNetworkState() {
		return NetworkState.CONFIGURATION;
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",EnterReconfigurationS2CPacket,"">(this);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",EnterReconfigurationS2CPacket,"">(this);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",EnterReconfigurationS2CPacket,"">(this, object);
	}
}
