package net.minecraft.test;

public interface TestListener {
	void onStarted(GameTest test);

	void onFailed(GameTest test);
}
