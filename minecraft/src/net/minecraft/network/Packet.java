package net.minecraft.network;

import java.io.IOException;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.util.PacketByteBuf;

public interface Packet<T extends PacketListener> {
	void read(PacketByteBuf buf) throws IOException;

	void write(PacketByteBuf buf) throws IOException;

	void apply(T listener);

	/**
	 * Returns whether a throwable in writing of this packet allows the
	 * connection to simply skip the packet's sending than disconnecting.
	 */
	default boolean isWritingErrorSkippable() {
		return false;
	}
}
