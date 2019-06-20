package net.minecraft;

public final class class_2987 extends RuntimeException {
	public static final class_2987 field_13400 = new class_2987();

	private class_2987() {
		this.setStackTrace(new StackTraceElement[0]);
	}

	public synchronized Throwable fillInStackTrace() {
		this.setStackTrace(new StackTraceElement[0]);
		return this;
	}
}
