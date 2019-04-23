package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Unit;

public interface ResourceReloadMonitor {
	CompletableFuture<Unit> whenComplete();

	@Environment(EnvType.CLIENT)
	float getProgress();

	@Environment(EnvType.CLIENT)
	boolean isLoadStageComplete();

	@Environment(EnvType.CLIENT)
	boolean isApplyStageComplete();

	@Environment(EnvType.CLIENT)
	void throwExceptions();
}
