package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ImageFilter {
	NativeImage filterImage(NativeImage nativeImage);

	void method_3238();
}
