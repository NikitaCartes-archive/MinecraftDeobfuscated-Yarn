package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public enum BlankGlyph implements RenderableGlyph {
	INSTANCE;

	private static final int WIDTH = 5;
	private static final int HEIGHT = 8;
	private static final NativeImage IMAGE = Util.make(new NativeImage(NativeImage.Format.RGBA, 5, 8, false), image -> {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 5; j++) {
				boolean bl = j == 0 || j + 1 == 5 || i == 0 || i + 1 == 8;
				image.setColor(j, i, bl ? -1 : 0);
			}
		}

		image.untrack();
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
