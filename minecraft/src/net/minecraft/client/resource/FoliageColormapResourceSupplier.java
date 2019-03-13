package net.minecraft.client.resource;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.FoliageColorHandler;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SupplyingResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class FoliageColormapResourceSupplier extends SupplyingResourceReloadListener<int[]> {
	private static final Identifier field_5303 = new Identifier("textures/colormap/foliage.png");

	protected int[] method_18660(ResourceManager resourceManager, Profiler profiler) {
		try {
			return RawTextureDataLoader.method_16049(resourceManager, field_5303);
		} catch (IOException var4) {
			throw new IllegalStateException("Failed to load foliage color texture", var4);
		}
	}

	protected void method_18659(int[] is, ResourceManager resourceManager, Profiler profiler) {
		FoliageColorHandler.setColorMap(is);
	}
}
