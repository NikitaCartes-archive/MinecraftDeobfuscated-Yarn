package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Void;

public interface ResourceReloadMonitor {
	CompletableFuture<Void> whenComplete();

	@Environment(EnvType.CLIENT)
	float getProgress();

	@Environment(EnvType.CLIENT)
	boolean isLoadStageComplete();

	@Environment(EnvType.CLIENT)
	boolean isApplyStageComplete();
}
