package net.minecraft.client.resource;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.biome.GrassColors;

@Environment(EnvType.CLIENT)
public class GrassColormapResourceSupplier extends SinglePreparationResourceReloader<int[]> {
	private static final Identifier GRASS_COLORMAP_LOC = Identifier.ofVanilla("textures/colormap/grass.png");

	protected int[] tryLoad(ResourceManager resourceManager, Profiler profiler) {
		try {
			return RawTextureDataLoader.loadRawTextureData(resourceManager, GRASS_COLORMAP_LOC);
		} catch (IOException var4) {
			throw new IllegalStateException("Failed to load grass color texture", var4);
		}
	}

	protected void apply(int[] is, ResourceManager resourceManager, Profiler profiler) {
		GrassColors.setColorMap(is);
	}
}
