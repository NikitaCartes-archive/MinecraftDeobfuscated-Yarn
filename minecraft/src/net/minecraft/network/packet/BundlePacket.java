package net.minecraft.network.packet;

import net.minecraft.network.listener.PacketListener;

public abstract class BundlePacket<T extends PacketListener> implements Packet<T> {
	private final Iterable<Packet<? super T>> packets;

	protected BundlePacket(Iterable<Packet<? super T>> packets) {
		this.packets = packets;
	}

	public final Iterable<Packet<? super T>> getPackets() {
		return this.packets;
	}

	@Override
	public abstract PacketType<? extends BundlePacket<T>> getPacketId();
}
