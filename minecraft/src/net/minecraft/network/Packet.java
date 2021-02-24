package net.minecraft.network;

import net.minecraft.network.listener.PacketListener;

public interface Packet<T extends PacketListener> {
	void write(PacketByteBuf buf);

	void apply(T listener);

	/**
	 * Returns whether a throwable in writing of this packet allows the
	 * connection to simply skip the packet's sending than disconnecting.
	 */
	default boolean isWritingErrorSkippable() {
		return false;
	}
}
