package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.Packet;

public class BundleS2CPacket extends BundlePacket<ClientPlayPacketListener> {
	public BundleS2CPacket(Iterable<Packet<ClientPlayPacketListener>> iterable) {
		super(iterable);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBundle(this);
	}
}
