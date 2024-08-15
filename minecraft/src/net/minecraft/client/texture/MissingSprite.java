package net.minecraft.client.texture;

import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public final class MissingSprite {
	private static final int WIDTH = 16;
	private static final int HEIGHT = 16;
	private static final String MISSINGNO_ID = "missingno";
	private static final Identifier MISSINGNO = Identifier.ofVanilla("missingno");
	private static final ResourceMetadata METADATA = new ResourceMetadata.Builder()
		.add(AnimationResourceMetadata.READER, new AnimationResourceMetadata(ImmutableList.of(new AnimationFrameResourceMetadata(0, -1)), 16, 16, 1, false))
		.build();
	@Nullable
	private static NativeImageBackedTexture texture;

	private static NativeImage createImage(int width, int height) {
		NativeImage nativeImage = new NativeImage(width, height, false);
		int i = -524040;

		for (int j = 0; j < height; j++) {
			for (int k = 0; k < width; k++) {
				if (j < height / 2 ^ k < width / 2) {
					nativeImage.setColorArgb(k, j, -524040);
				} else {
					nativeImage.setColorArgb(k, j, -16777216);
				}
			}
		}

		return nativeImage;
	}

	public static SpriteContents createSpriteContents() {
		NativeImage nativeImage = createImage(16, 16);
		return new SpriteContents(MISSINGNO, new SpriteDimensions(16, 16), nativeImage, METADATA);
	}

	public static Identifier getMissingSpriteId() {
		return MISSINGNO;
	}

	public static NativeImageBackedTexture getMissingSpriteTexture() {
		if (texture == null) {
			NativeImage nativeImage = createImage(16, 16);
			nativeImage.untrack();
			texture = new NativeImageBackedTexture(nativeImage);
			MinecraftClient.getInstance().getTextureManager().registerTexture(MISSINGNO, texture);
		}

		return texture;
	}
}
