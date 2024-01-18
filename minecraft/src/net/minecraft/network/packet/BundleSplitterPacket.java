package net.minecraft.network.packet;

import net.minecraft.network.listener.PacketListener;

public abstract class BundleSplitterPacket<T extends PacketListener> implements Packet<T> {
	@Override
	public final void apply(T listener) {
		throw new AssertionError("This packet should be handled by pipeline");
	}

	@Override
	public abstract PacketType<? extends BundleSplitterPacket<T>> getPacketId();
}
