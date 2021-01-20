package net.minecraft.test;

public interface TestCompletionListener {
	void onTestFailed(GameTest test);

	void onTestPassed(GameTest test);
}
