package net.minecraft.client.resource;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3685;
import net.minecraft.client.render.block.FoliageColorHandler;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FoliageColormapResourceLoader implements ResourceReloadListener {
	private static final Identifier FOLIAGE_COLORMAP_LOC = new Identifier("textures/colormap/foliage.png");

	@Override
	public void onResourceReload(ResourceManager resourceManager) {
		try {
			FoliageColorHandler.setColorMap(class_3685.method_16049(resourceManager, FOLIAGE_COLORMAP_LOC));
		} catch (IOException var3) {
		}
	}
}
