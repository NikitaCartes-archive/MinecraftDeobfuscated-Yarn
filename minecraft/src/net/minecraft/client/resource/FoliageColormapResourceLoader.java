package net.minecraft.client.resource;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3685;
import net.minecraft.class_4080;
import net.minecraft.client.render.block.FoliageColorHandler;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class FoliageColormapResourceLoader extends class_4080<int[]> {
	private static final Identifier FOLIAGE_COLORMAP_LOC = new Identifier("textures/colormap/foliage.png");

	protected int[] method_18660(ResourceManager resourceManager, Profiler profiler) {
		try {
			return class_3685.method_16049(resourceManager, FOLIAGE_COLORMAP_LOC);
		} catch (IOException var4) {
			throw new IllegalStateException("Failed to load foliage color texture", var4);
		}
	}

	protected void method_18659(int[] is, ResourceManager resourceManager, Profiler profiler) {
		FoliageColorHandler.setColorMap(is);
	}
}
