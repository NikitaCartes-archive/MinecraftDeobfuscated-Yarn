package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public enum BlankGlyph implements RenderableGlyph {
	field_2283;

	private static final NativeImage IMAGE = Util.make(new NativeImage(NativeImage.Format.ABGR, 5, 8, false), nativeImage -> {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 5; j++) {
				boolean bl = j == 0 || j + 1 == 5 || i == 0 || i + 1 == 8;
				nativeImage.setPixelColor(j, i, bl ? -1 : 0);
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
