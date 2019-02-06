package net.minecraft.client.resource;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3685;
import net.minecraft.client.render.block.FoliageColorHandler;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class FoliageColormapResourceLoader implements ResourceReloadListener<int[]> {
	private static final Identifier FOLIAGE_COLORMAP_LOC = new Identifier("textures/colormap/foliage.png");

	@Override
	public CompletableFuture<int[]> prepare(ResourceManager resourceManager, Profiler profiler) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return class_3685.method_16049(resourceManager, FOLIAGE_COLORMAP_LOC);
			} catch (IOException var2) {
				return null;
			}
		});
	}

	public void method_18171(ResourceManager resourceManager, int[] is, Profiler profiler) {
		FoliageColorHandler.setColorMap(is);
	}
}
