package net.minecraft.network;

/**
 * An exception thrown on netty's event loop to quit handling of one packet,
 * usually as it is scheduled to be handled on the game engine thread.
 * 
 * <p>This is a {@linkplain #INSTANCE singleton}. It ignores stack traces
 * in order to be efficient.
 * 
 * @see NetworkThreadUtils
 * @see ClientConnection#channelRead0
 */
public final class OffThreadException extends RuntimeException {
	/**
	 * The singleton instance, to reduce object allocations.
	 */
	public static final OffThreadException INSTANCE = new OffThreadException();

	private OffThreadException() {
		this.setStackTrace(new StackTraceElement[0]);
	}

	public synchronized Throwable fillInStackTrace() {
		this.setStackTrace(new StackTraceElement[0]);
		return this;
	}
}
