package net.minecraft.util;

/**
 * An exception thrown when parsing or constructing an {@link Identifier}
 * that contains an invalid character. This should not be caught, instead
 * {@link Identifier#tryParse} or {@link Identifier#of} should be used.
 */
public class InvalidIdentifierException extends RuntimeException {
	public InvalidIdentifierException(String message) {
		super(message);
	}

	public InvalidIdentifierException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
