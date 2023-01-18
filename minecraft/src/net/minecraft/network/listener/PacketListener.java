package net.minecraft.network.listener;

import net.minecraft.text.Text;

/**
 * A packet listener listens to packets on a {@linkplain
 * net.minecraft.network.ClientConnection connection}.
 * 
 * <p>Its listener methods will be called on the netty event loop than the
 * client or server game engine threads.
 */
public interface PacketListener {
	/**
	 * Called when the connection this listener listens to has disconnected.
	 * Can be used to display the disconnection reason.
	 * 
	 * @param reason the reason of disconnection; may be a generic message
	 */
	void onDisconnected(Text reason);

	boolean isConnectionOpen();

	/**
	 * {@return whether uncaught exceptions in main thread should crash the game
	 * instead of logging and ignoring them}
	 * 
	 * @implNote This is {@code true} by default.
	 * 
	 * @apiNote This only affects the processing on the main thread done by calling
	 * methods in {@link net.minecraft.network.NetworkThreadUtils}. Uncaught exceptions
	 * in other threads or processing in the main thread using the {@code client.execute(() -> {})}
	 * code will be unaffected, and always gets logged without crashing.
	 * 
	 * @see ServerPacketListener
	 */
	default boolean shouldCrashOnException() {
		return true;
	}
}
