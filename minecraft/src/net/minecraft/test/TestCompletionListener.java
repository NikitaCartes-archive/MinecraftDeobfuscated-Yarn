package net.minecraft.test;

public interface TestCompletionListener {
	void onTestFailed(GameTestState test);

	void onTestPassed(GameTestState test);

	default void onStopped() {
	}
}
