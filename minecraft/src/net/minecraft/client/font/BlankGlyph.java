package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_383;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public enum BlankGlyph implements class_383 {
	INSTANCE;

	private static final NativeImage field_2281 = SystemUtil.consume(new NativeImage(NativeImage.Format.field_4997, 5, 8, false), nativeImage -> {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 5; j++) {
				boolean bl = j == 0 || j + 1 == 5 || i == 0 || i + 1 == 8;
				nativeImage.setPixelRGBA(j, i, bl ? -1 : 0);
			}
		}

		nativeImage.method_4302();
	});

	@Override
	public int method_2031() {
		return 5;
	}

	@Override
	public int method_2032() {
		return 8;
	}

	@Override
	public float getAdvance() {
		return 6.0F;
	}

	@Override
	public float method_2035() {
		return 1.0F;
	}

	@Override
	public void method_2030(int i, int j) {
		field_2281.upload(0, i, j, false);
	}

	@Override
	public boolean method_2033() {
		return true;
	}
}
