package net.minecraft.network.listener;

/**
 * The interface for serverbound packet listeners.
 * 
 * @implNote Serverbound packet listeners log any uncaught exceptions
 * without crashing.
 */
public interface ServerCrashSafePacketListener extends ServerPacketListener {
	@Override
	default boolean shouldCrashOnException() {
		return false;
	}
}
