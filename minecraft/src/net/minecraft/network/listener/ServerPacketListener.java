package net.minecraft.network.listener;

/**
 * The base interface for serverbound packet listeners.
 * 
 * @implNote Serverbound packet listeners log any uncaught exceptions
 * without crashing.
 */
public interface ServerPacketListener extends PacketListener {
	@Override
	default boolean shouldCrashOnException() {
		return false;
	}
}
