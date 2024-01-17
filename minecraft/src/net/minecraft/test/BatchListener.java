package net.minecraft.test;

public interface BatchListener {
	void onStarted(GameTestBatch batch);

	void onFinished(GameTestBatch batch);
}
