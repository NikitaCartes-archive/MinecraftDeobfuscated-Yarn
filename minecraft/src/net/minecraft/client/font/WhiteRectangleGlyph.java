package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public enum WhiteRectangleGlyph implements RenderableGlyph {
	INSTANCE;

	private static final NativeImage IMAGE = Util.make(new NativeImage(NativeImage.Format.RGBA, 5, 8, false), nativeImage -> {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 5; j++) {
				if (j != 0 && j + 1 != 5 && i != 0 && i + 1 != 8) {
					boolean var4 = false;
				} else {
					boolean var10000 = true;
				}

				nativeImage.setPixelRgba(j, i, -1);
			}
		}

		nativeImage.untrack();
	});

	@Override
	public int getWidth() {
		return 5;
	}

	@Override
	public int getHeight() {
		return 8;
	}

	@Override
	public float getAdvance() {
		return 6.0F;
	}

	@Override
	public float getOversample() {
		return 1.0F;
	}

	@Override
	public void upload(int x, int y) {
		IMAGE.upload(0, x, y, false);
	}

	@Override
	public boolean hasColor() {
		return true;
	}
}
