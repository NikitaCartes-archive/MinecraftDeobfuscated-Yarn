package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.network.packet.PlayPackets;

public class BundleS2CPacket extends BundlePacket<ClientPlayPacketListener> {
	public BundleS2CPacket(Iterable<Packet<? super ClientPlayPacketListener>> iterable) {
		super(iterable);
	}

	@Override
	public PacketIdentifier<BundleS2CPacket> getPacketId() {
		return PlayPackets.BUNDLE;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBundle(this);
	}
}
