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
	private static NativeImageBackedTexture texture;
	private static final Lazy<NativeImage> IMAGE = new Lazy<>(() -> {
		NativeImage nativeImage = new NativeImage(16, 16, false);
		int i = -16777216;
		int j = -524040;

		for (int k = 0; k < 16; k++) {
			for (int l = 0; l < 16; l++) {
				if (k < 8 ^ l < 8) {
					nativeImage.setPixelColor(l, k, -524040);
				} else {
					nativeImage.setPixelColor(l, k, -16777216);
				}
			}
		}

		nativeImage.untrack();
		return nativeImage;
	});
	private static final Sprite.Info INFO = new Sprite.Info(
		MISSINGNO,
		16,
		16,
		new AnimationResourceMetadata(Lists.<AnimationFrameResourceMetadata>newArrayList(new AnimationFrameResourceMetadata(0, -1)), 16, 16, 1, false)
	);

	private MissingSprite(SpriteAtlasTexture spriteAtlasTexture, int maxLevel, int atlasWidth, int atlasHeight, int x, int y) {
		super(spriteAtlasTexture, INFO, maxLevel, atlasWidth, atlasHeight, x, y, IMAGE.get());
	}

	public static MissingSprite getMissingSprite(SpriteAtlasTexture spriteAtlasTexture, int maxLevel, int atlasWidth, int atlasHeight, int x, int y) {
		return new MissingSprite(spriteAtlasTexture, maxLevel, atlasWidth, atlasHeight, x, y);
	}

	public static Identifier getMissingSpriteId() {
		return MISSINGNO;
	}

	public static Sprite.Info getMissingInfo() {
		return INFO;
	}

	@Override
	public void close() {
		for (int i = 1; i < this.images.length; i++) {
			this.images[i].close();
		}
	}

	public static NativeImageBackedTexture getMissingSpriteTexture() {
		if (texture == null) {
			texture = new NativeImageBackedTexture(IMAGE.get());
			MinecraftClient.getInstance().getTextureManager().registerTexture(MISSINGNO, texture);
		}

		return texture;
	}
}
