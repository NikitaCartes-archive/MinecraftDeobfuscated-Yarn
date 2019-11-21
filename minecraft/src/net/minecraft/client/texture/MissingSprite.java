package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;

@Environment(EnvType.CLIENT)
public final class MissingSprite extends Sprite {
	private static final Identifier MISSINGNO = new Identifier("missingno");
	@Nullable
	private static NativeImageBackedTexture TEXTURE;
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
	private static final Sprite.class_4727 field_21748 = new Sprite.class_4727(
		MISSINGNO,
		16,
		16,
		new AnimationResourceMetadata(Lists.<AnimationFrameResourceMetadata>newArrayList(new AnimationFrameResourceMetadata(0, -1)), 16, 16, 1, false)
	);

	private MissingSprite(SpriteAtlasTexture spriteAtlasTexture, int i, int j, int k, int l, int m) {
		super(spriteAtlasTexture, field_21748, i, j, k, l, m, IMAGE.get());
	}

	public static MissingSprite getMissingSprite(SpriteAtlasTexture spriteAtlasTexture, int i, int j, int k, int l, int m) {
		return new MissingSprite(spriteAtlasTexture, i, j, k, l, m);
	}

	public static Identifier getMissingSpriteId() {
		return MISSINGNO;
	}

	public static Sprite.class_4727 method_24104() {
		return field_21748;
	}

	@Override
	public void destroy() {
		for (int i = 1; i < this.images.length; i++) {
			this.images[i].close();
		}
	}

	public static NativeImageBackedTexture getMissingSpriteTexture() {
		if (TEXTURE == null) {
			TEXTURE = new NativeImageBackedTexture(IMAGE.get());
			MinecraftClient.getInstance().getTextureManager().registerTexture(MISSINGNO, TEXTURE);
		}

		return TEXTURE;
	}
}
