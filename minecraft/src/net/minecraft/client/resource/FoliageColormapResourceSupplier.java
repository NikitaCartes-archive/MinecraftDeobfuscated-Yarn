package net.minecraft.client.resource;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.FoliageColorHandler;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceSupplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class FoliageColormapResourceSupplier extends ResourceSupplier<int[]> {
	private static final Identifier FOLIAGE_COLORMAP_LOC = new Identifier("textures/colormap/foliage.png");

	protected int[] method_18660(ResourceManager resourceManager, Profiler profiler) {
		try {
			return RawTextureDataLoader.loadRawTextureData(resourceManager, FOLIAGE_COLORMAP_LOC);
		} catch (IOException var4) {
			throw new IllegalStateException("Failed to load foliage color texture", var4);
		}
	}

	protected void method_18659(int[] is, ResourceManager resourceManager, Profiler profiler) {
		FoliageColorHandler.setColorMap(is);
	}
}
