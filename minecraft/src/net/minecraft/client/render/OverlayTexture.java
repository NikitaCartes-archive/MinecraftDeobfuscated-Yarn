package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

@Environment(EnvType.CLIENT)
public class OverlayTexture implements AutoCloseable {
	public static final int field_21444 = method_23625(0, 10);
	private final NativeImageBackedTexture texture = new NativeImageBackedTexture(16, 16, false);

	public OverlayTexture() {
		NativeImage nativeImage = this.texture.getImage();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if (i < 8) {
					nativeImage.setPixelRGBA(j, i, -1308622593);
				} else {
					int k = (int)((1.0F - (float)j / 15.0F * 0.2F) * 255.0F);
					nativeImage.setPixelRGBA(j, i, k << 24 | 16777215);
				}
			}
		}

		RenderSystem.activeTexture(33985);
		this.texture.method_23207();
		RenderSystem.matrixMode(5890);
		RenderSystem.loadIdentity();
		float f = 0.06666667F;
		RenderSystem.scalef(0.06666667F, 0.06666667F, 0.06666667F);
		RenderSystem.matrixMode(5888);
		this.texture.method_23207();
		nativeImage.method_22619(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), false, true, false, false);
		RenderSystem.activeTexture(33984);
	}

	public void close() {
		this.texture.close();
	}

	public void setupOverlayColor() {
		RenderSystem.setupOverlayColor(this.texture::getGlId, 16);
	}

	public static int getU(float f) {
		return (int)(f * 15.0F);
	}

	public static int getV(boolean bl) {
		return bl ? 3 : 10;
	}

	public static int method_23625(int i, int j) {
		return i | j << 16;
	}

	public static int method_23624(float f, boolean bl) {
		return method_23625(getU(f), getV(bl));
	}

	public void teardownOverlayColor() {
		RenderSystem.teardownOverlayColor();
	}
}
