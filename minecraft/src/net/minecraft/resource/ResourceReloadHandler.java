package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Void;

public interface ResourceReloadHandler {
	CompletableFuture<Void> whenComplete();

	@Environment(EnvType.CLIENT)
	float getProgress();

	@Environment(EnvType.CLIENT)
	boolean method_18786();

	@Environment(EnvType.CLIENT)
	boolean method_18787();
}
