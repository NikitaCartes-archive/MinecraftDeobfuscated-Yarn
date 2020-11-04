package net.minecraft.network;

public final class OffThreadException extends RuntimeException {
	public static final OffThreadException INSTANCE = new OffThreadException();

	private OffThreadException() {
		this.setStackTrace(new StackTraceElement[0]);
	}

	public synchronized Throwable fillInStackTrace() {
		this.setStackTrace(new StackTraceElement[0]);
		return this;
	}
}
