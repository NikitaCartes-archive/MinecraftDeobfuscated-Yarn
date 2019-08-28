package net.minecraft.test;

public interface TestCompletionListener {
	void onTestFailed(GameTest gameTest);

	void onTestPassed(GameTest gameTest);
}
