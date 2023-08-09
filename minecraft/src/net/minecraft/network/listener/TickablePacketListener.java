package net.minecraft.network.listener;

public interface TickablePacketListener extends PacketListener {
	/**
	 * Ticks this packet listener on the game engine thread.  The listener is responsible
	 * for synchronizing between the game engine and netty event loop threads.
	 */
	void tick();
}
