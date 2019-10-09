package net.minecraft.test;

public interface TestListener {
	void onStarted(GameTest gameTest);

	void onFailed(GameTest gameTest);
}
