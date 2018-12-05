package net.minecraft.client.resource;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3685;
import net.minecraft.client.render.block.GrassColorHandler;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GrassColormapResourceLoader implements ResourceReloadListener {
	private static final Identifier GRASS_COLORMAP_LOC = new Identifier("textures/colormap/grass.png");

	@Override
	public void onResourceReload(ResourceManager resourceManager) {
		try {
			GrassColorHandler.setColorMap(class_3685.method_16049(resourceManager, GRASS_COLORMAP_LOC));
		} catch (IOException var3) {
		}
	}
}
