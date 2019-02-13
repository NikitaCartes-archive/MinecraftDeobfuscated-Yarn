package net.minecraft.client.resource;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3685;
import net.minecraft.client.render.block.FoliageColorHandler;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class FoliageColormapResourceLoader implements ResourceReloadListener {
	private static final Identifier FOLIAGE_COLORMAP_LOC = new Identifier("textures/colormap/foliage.png");

	@Override
	public CompletableFuture<Void> apply(
		ResourceReloadListener.Helper helper, ResourceManager resourceManager, Profiler profiler, Profiler profiler2, Executor executor, Executor executor2
	) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return class_3685.method_16049(resourceManager, FOLIAGE_COLORMAP_LOC);
			} catch (IOException var2) {
				return null;
			}
		}, executor).thenCompose(helper::waitForAll).thenAcceptAsync(FoliageColorHandler::setColorMap, executor2);
	}
}
