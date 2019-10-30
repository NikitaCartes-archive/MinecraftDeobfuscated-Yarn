package net.minecraft.server;

public interface WorldGenerationProgressListenerFactory {
	WorldGenerationProgressListener create(int radius);
}
