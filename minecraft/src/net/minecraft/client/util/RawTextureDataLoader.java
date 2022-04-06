package net.minecraft.client.util;

import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RawTextureDataLoader {
	@Deprecated
	public static int[] loadRawTextureData(ResourceManager resourceManager, Identifier id) throws IOException {
		InputStream inputStream = resourceManager.open(id);

		int[] var4;
		try (NativeImage nativeImage = NativeImage.read(inputStream)) {
			var4 = nativeImage.makePixelArray();
		} catch (Throwable var9) {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Throwable var6) {
					var9.addSuppressed(var6);
				}
			}

			throw var9;
		}

		if (inputStream != null) {
			inputStream.close();
		}

		return var4;
	}
}
