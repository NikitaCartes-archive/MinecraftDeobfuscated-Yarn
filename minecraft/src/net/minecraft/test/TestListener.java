package net.minecraft.test;

public interface TestListener {
	void onStarted(GameTestState test);

	void onPassed(GameTestState test);

	void onFailed(GameTestState test);
}
