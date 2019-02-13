package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Void;

public interface ResourceReloadHandler {
	CompletableFuture<Void> whenComplete();

	@Environment(EnvType.CLIENT)
	float getProgress();
}
