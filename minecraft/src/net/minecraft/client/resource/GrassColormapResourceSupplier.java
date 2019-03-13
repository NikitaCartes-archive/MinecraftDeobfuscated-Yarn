package net.minecraft.client.resource;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.GrassColorHandler;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SupplyingResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class GrassColormapResourceSupplier extends SupplyingResourceReloadListener<int[]> {
	private static final Identifier field_5302 = new Identifier("textures/colormap/grass.png");

	protected int[] method_18662(ResourceManager resourceManager, Profiler profiler) {
		try {
			return RawTextureDataLoader.method_16049(resourceManager, field_5302);
		} catch (IOException var4) {
			throw new IllegalStateException("Failed to load grass color texture", var4);
		}
	}

	protected void method_18661(int[] is, ResourceManager resourceManager, Profiler profiler) {
		GrassColorHandler.setColorMap(is);
	}
}
