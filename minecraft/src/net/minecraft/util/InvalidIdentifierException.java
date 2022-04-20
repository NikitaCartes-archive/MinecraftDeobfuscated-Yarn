package net.minecraft.util;

public class InvalidIdentifierException extends RuntimeException {
	public InvalidIdentifierException(String string) {
		super(string);
	}

	public InvalidIdentifierException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
