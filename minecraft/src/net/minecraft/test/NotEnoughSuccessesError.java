package net.minecraft.test;

class NotEnoughSuccessesError extends Throwable {
	public NotEnoughSuccessesError(int attempts, int successes, GameTestState test) {
		super(
			"Not enough successes: "
				+ successes
				+ " out of "
				+ attempts
				+ " attempts. Required successes: "
				+ test.getRequiredSuccesses()
				+ ". max attempts: "
				+ test.getMaxAttempts()
				+ ".",
			test.getThrowable()
		);
	}
}
