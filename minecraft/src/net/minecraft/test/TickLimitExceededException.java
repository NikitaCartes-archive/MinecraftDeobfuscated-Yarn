package net.minecraft.test;

public class TickLimitExceededException extends RuntimeException {
	public TickLimitExceededException(String message) {
		super(message);
	}
}
