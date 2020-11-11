package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.damage.DamageSource;

@Environment(EnvType.CLIENT)
public class OverlayTexture implements AutoCloseable {
	public static final int DEFAULT_UV = packUv(0, 10);
	private final NativeImageBackedTexture texture = new NativeImageBackedTexture(24, 24, false);

	public OverlayTexture() {
		NativeImage nativeImage = this.texture.getImage();

		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 24; j++) {
				if (i < 8) {
					nativeImage.setPixelColor(j, i, -1308622593);
				} else if (i < 16) {
					int k = (int)((1.0F - (float)j / 15.0F * 0.75F) * 255.0F);
					nativeImage.setPixelColor(j, i, k << 24 | 16777215);
				} else {
					nativeImage.setPixelColor(j, i, -1291911168);
				}
			}
		}

		RenderSystem.activeTexture(33985);
		this.texture.bindTexture();
		RenderSystem.matrixMode(5890);
		RenderSystem.loadIdentity();
		float f = 0.04347826F;
		RenderSystem.scalef(0.04347826F, 0.04347826F, 0.04347826F);
		RenderSystem.matrixMode(5888);
		this.texture.bindTexture();
		nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), false, true, false, false);
		RenderSystem.activeTexture(33984);
	}

	public void close() {
		this.texture.close();
	}

	public void setupOverlayColor() {
		RenderSystem.setupOverlayColor(this.texture::getGlId, 24);
	}

	public static int getU(float whiteOverlayProgress) {
		return (int)(whiteOverlayProgress * 23.0F);
	}

	public static int getV(boolean hurt) {
		return method_32692(hurt, null);
	}

	public static int method_32692(boolean bl, @Nullable DamageSource damageSource) {
		if (bl) {
			return damageSource == DamageSource.FREEZE ? 19 : 3;
		} else {
			return 10;
		}
	}

	public static int packUv(int u, int v) {
		return u | v << 16;
	}

	public static int getUv(float f, boolean hurt) {
		return packUv(getU(f), getV(hurt));
	}

	public void teardownOverlayColor() {
		RenderSystem.teardownOverlayColor();
	}
}
