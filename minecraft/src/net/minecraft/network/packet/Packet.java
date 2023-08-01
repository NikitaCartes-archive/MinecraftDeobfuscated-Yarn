package net.minecraft.network.packet;

import javax.annotation.Nullable;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;

public interface Packet<T extends PacketListener> {
	void write(PacketByteBuf buf);

	void apply(T listener);

	/**
	 * {@return whether a throwable in writing of this packet allows the
	 * connection to simply skip the packet's sending than disconnecting}
	 */
	default boolean isWritingErrorSkippable() {
		return false;
	}

	/**
	 * {@return a new network state to transition to, or {@code null}
	 * to indicate no state change}
	 */
	@Nullable
	default NetworkState getNewNetworkState() {
		return null;
	}
}
