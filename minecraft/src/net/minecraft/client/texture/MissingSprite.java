package net.minecraft.client.texture;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;

@Environment(EnvType.CLIENT)
public final class MissingSprite extends Sprite {
	private static final Identifier MISSINGNO = new Identifier("missingno");
	@Nullable
	private static NativeImageBackedTexture texture;
	private static final Lazy<NativeImage> IMAGE = new Lazy<>(() -> {
		NativeImage nativeImage = new NativeImage(16, 16, false);
		int i = -16777216;
		int j = -524040;

		for (int k = 0; k < 16; k++) {
			for (int l = 0; l < 16; l++) {
				if (k < 8 ^ l < 8) {
					nativeImage.setPixelRgba(l, k, -524040);
				} else {
					nativeImage.setPixelRgba(l, k, -16777216);
				}
			}
		}

		nativeImage.untrack();
		return nativeImage;
	});

	private MissingSprite() {
		super(MISSINGNO, 16, 16);
		this.images = new NativeImage[]{IMAGE.get()};
	}

	public static MissingSprite getMissingSprite() {
		return new MissingSprite();
	}

	public static Identifier getMissingSpriteId() {
		return MISSINGNO;
	}

	@Override
	public void destroy() {
		for (int i = 1; i < this.images.length; i++) {
			this.images[i].close();
		}

		this.images = new NativeImage[]{IMAGE.get()};
	}

	public static NativeImageBackedTexture getMissingSpriteTexture() {
		if (texture == null) {
			texture = new NativeImageBackedTexture(IMAGE.get());
			MinecraftClient.getInstance().getTextureManager().registerTexture(MISSINGNO, texture);
		}

		return texture;
	}
}
