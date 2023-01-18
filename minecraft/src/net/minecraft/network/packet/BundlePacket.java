package net.minecraft.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;

public abstract class BundlePacket<T extends PacketListener> implements Packet<T> {
	private final Iterable<Packet<T>> packets;

	protected BundlePacket(Iterable<Packet<T>> packets) {
		this.packets = packets;
	}

	public final Iterable<Packet<T>> getPackets() {
		return this.packets;
	}

	@Override
	public final void write(PacketByteBuf buf) {
	}
}
