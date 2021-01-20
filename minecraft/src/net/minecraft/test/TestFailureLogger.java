package net.minecraft.test;

public class TestFailureLogger {
	private static TestCompletionListener completionListener = new FailureLoggingTestCompletionListener();

	public static void failTest(GameTest test) {
		completionListener.onTestFailed(test);
	}

	public static void passTest(GameTest test) {
		completionListener.onTestPassed(test);
	}
}
