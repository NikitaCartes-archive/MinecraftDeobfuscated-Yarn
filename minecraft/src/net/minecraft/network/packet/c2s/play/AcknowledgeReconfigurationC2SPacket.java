package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

public record AcknowledgeReconfigurationC2SPacket() implements Packet<ServerPlayPacketListener> {
	public AcknowledgeReconfigurationC2SPacket(PacketByteBuf buf) {
		this();
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
}
