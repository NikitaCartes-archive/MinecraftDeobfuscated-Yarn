package net.minecraft.client.texture;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public final class MissingSprite extends Sprite {
	private static final Identifier MISSINGNO = new Identifier("missingno");
	@Nullable
	private static NativeImageBackedTexture field_5220;
	private static final NativeImage IMAGE = new NativeImage(16, 16, false);
	private static final MissingSprite INSTANCE = SystemUtil.get(() -> {
		MissingSprite missingSprite = new MissingSprite();
		int i = -16777216;
		int j = -524040;

		for (int k = 0; k < 16; k++) {
			for (int l = 0; l < 16; l++) {
				if (k < 8 ^ l < 8) {
					IMAGE.setPixelRGBA(l, k, -524040);
				} else {
					IMAGE.setPixelRGBA(l, k, -16777216);
				}
			}
		}

		IMAGE.method_4302();
		return missingSprite;
	});

	private MissingSprite() {
		super(MISSINGNO, 16, 16);
		this.images = new NativeImage[1];
		this.images[0] = IMAGE;
	}

	public static MissingSprite getMissingSprite() {
		return INSTANCE;
	}

	public static Identifier getMissingTextureId() {
		return MISSINGNO;
	}

	@Override
	public void destroy() {
		for (int i = 1; i < this.images.length; i++) {
			this.images[i].close();
		}

		this.images = new NativeImage[1];
		this.images[0] = IMAGE;
	}

	public static NativeImageBackedTexture method_4540() {
		if (field_5220 == null) {
			field_5220 = new NativeImageBackedTexture(IMAGE);
			MinecraftClient.getInstance().getTextureManager().registerTexture(MISSINGNO, field_5220);
		}

		return field_5220;
	}
}
