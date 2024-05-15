package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

@Environment(EnvType.CLIENT)
public class OverlayTexture implements AutoCloseable {
	private static final int field_32956 = 16;
	public static final int field_32953 = 0;
	public static final int field_32954 = 3;
	public static final int field_32955 = 10;
	public static final int DEFAULT_UV = packUv(0, 10);
	private final NativeImageBackedTexture texture = new NativeImageBackedTexture(16, 16, false);

	public OverlayTexture() {
		NativeImage nativeImage = this.texture.getImage();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if (i < 8) {
					nativeImage.setColor(j, i, -1308622593);
				} else {
					int k = (int)((1.0F - (float)j / 15.0F * 0.75F) * 255.0F);
					nativeImage.setColor(j, i, k << 24 | 16777215);
				}
			}
		}

		RenderSystem.activeTexture(GlConst.GL_TEXTURE1);
		this.texture.bindTexture();
		nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), false, true, false, false);
		RenderSystem.activeTexture(GlConst.GL_TEXTURE0);
	}

	public void close() {
		this.texture.close();
	}

	public void setupOverlayColor() {
		RenderSystem.setupOverlayColor(this.texture.getGlId(), 16);
	}

	public static int getU(float whiteOverlayProgress) {
		return (int)(whiteOverlayProgress * 15.0F);
	}

	public static int getV(boolean hurt) {
		return hurt ? 3 : 10;
	}

	public static int packUv(int u, int v) {
		return u | v << 16;
	}

	public static int getUv(float whiteOverlayProgress, boolean hurt) {
		return packUv(getU(whiteOverlayProgress), getV(hurt));
	}

	public void teardownOverlayColor() {
		RenderSystem.teardownOverlayColor();
	}
}
