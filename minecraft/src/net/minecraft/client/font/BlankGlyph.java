package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public enum BlankGlyph implements RenderableGlyph {
	field_2283;

	private static final NativeImage IMAGE = SystemUtil.consume(new NativeImage(NativeImage.Format.field_4997, 5, 8, false), nativeImage -> {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 5; j++) {
				boolean bl = j == 0 || j + 1 == 5 || i == 0 || i + 1 == 8;
				nativeImage.setPixelRGBA(j, i, bl ? -1 : 0);
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
	public void upload(int i, int j) {
		IMAGE.upload(0, i, j, false);
	}

	@Override
	public boolean hasColor() {
		return true;
	}
}
