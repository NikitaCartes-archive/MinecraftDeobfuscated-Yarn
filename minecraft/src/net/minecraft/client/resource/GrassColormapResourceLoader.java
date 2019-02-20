package net.minecraft.client.resource;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3685;
import net.minecraft.class_4080;
import net.minecraft.client.render.block.GrassColorHandler;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class GrassColormapResourceLoader extends class_4080<int[]> {
	private static final Identifier GRASS_COLORMAP_LOC = new Identifier("textures/colormap/grass.png");

	protected int[] method_18662(ResourceManager resourceManager, Profiler profiler) {
		try {
			return class_3685.method_16049(resourceManager, GRASS_COLORMAP_LOC);
		} catch (IOException var4) {
			throw new IllegalStateException("Failed to load grass color texture", var4);
		}
	}

	protected void method_18661(int[] is, ResourceManager resourceManager, Profiler profiler) {
		GrassColorHandler.setColorMap(is);
	}
}
