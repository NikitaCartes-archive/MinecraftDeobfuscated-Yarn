package net.minecraft.network.packet.c2s.play;

import java.lang.runtime.ObjectMethods;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

public final class AcknowledgeReconfigurationC2SPacket extends Record implements Packet<ServerPlayPacketListener> {
	public AcknowledgeReconfigurationC2SPacket(PacketByteBuf buf) {
		this();
	}

	public AcknowledgeReconfigurationC2SPacket() {
	}

	@Override
	public void write(PacketByteBuf buf) {
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onAcknowledgeReconfiguration(this);
	}

	@Override
	public NetworkState getNewNetworkState() {
		return NetworkState.CONFIGURATION;
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",AcknowledgeReconfigurationC2SPacket,"">(this);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",AcknowledgeReconfigurationC2SPacket,"">(this);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",AcknowledgeReconfigurationC2SPacket,"">(this, object);
	}
}
