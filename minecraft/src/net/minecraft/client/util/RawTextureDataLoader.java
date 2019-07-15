package net.minecraft.client.util;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RawTextureDataLoader {
	@Deprecated
	public static int[] loadRawTextureData(ResourceManager resourceManager, Identifier identifier) throws IOException {
		Resource resource = resourceManager.getResource(identifier);
		Throwable var3 = null;

		int[] var6;
		try (NativeImage nativeImage = NativeImage.read(resource.getInputStream())) {
			var6 = nativeImage.makePixelArray();
		} catch (Throwable var31) {
			var3 = var31;
			throw var31;
		} finally {
			if (resource != null) {
				if (var3 != null) {
					try {
						resource.close();
					} catch (Throwable var27) {
						var3.addSuppressed(var27);
					}
				} else {
					resource.close();
				}
			}
		}

		return var6;
	}
}
