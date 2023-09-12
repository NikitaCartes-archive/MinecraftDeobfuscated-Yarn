package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public record EnterReconfigurationS2CPacket() implements Packet<ClientPlayPacketListener> {
	public EnterReconfigurationS2CPacket(PacketByteBuf buf) {
		this();
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
}
