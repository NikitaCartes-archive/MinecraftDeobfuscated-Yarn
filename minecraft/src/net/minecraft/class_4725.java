package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class class_4725 {
	private static final float[] field_21747 = Util.create(new float[256], fs -> {
		for (int i = 0; i < fs.length; i++) {
			fs[i] = (float)Math.pow((double)((float)i / 255.0F), 2.2);
		}
	});

	public static NativeImage[] method_24102(NativeImage nativeImage, int i) {
		NativeImage[] nativeImages = new NativeImage[i + 1];
		nativeImages[0] = nativeImage;
		if (i > 0) {
			boolean bl = false;

			label51:
			for (int j = 0; j < nativeImage.getWidth(); j++) {
				for (int k = 0; k < nativeImage.getHeight(); k++) {
					if (nativeImage.getPixelRgba(j, k) >> 24 == 0) {
						bl = true;
						break label51;
					}
				}
			}

			for (int j = 1; j <= i; j++) {
				NativeImage nativeImage2 = nativeImages[j - 1];
				NativeImage nativeImage3 = new NativeImage(nativeImage2.getWidth() >> 1, nativeImage2.getHeight() >> 1, false);
				int l = nativeImage3.getWidth();
				int m = nativeImage3.getHeight();

				for (int n = 0; n < l; n++) {
					for (int o = 0; o < m; o++) {
						nativeImage3.setPixelRgba(
							n,
							o,
							method_24101(
								nativeImage2.getPixelRgba(n * 2 + 0, o * 2 + 0),
								nativeImage2.getPixelRgba(n * 2 + 1, o * 2 + 0),
								nativeImage2.getPixelRgba(n * 2 + 0, o * 2 + 1),
								nativeImage2.getPixelRgba(n * 2 + 1, o * 2 + 1),
								bl
							)
						);
					}
				}

				nativeImages[j] = nativeImage3;
			}
		}

		return nativeImages;
	}

	private static int method_24101(int i, int j, int k, int l, boolean bl) {
		if (bl) {
			float f = 0.0F;
			float g = 0.0F;
			float h = 0.0F;
			float m = 0.0F;
			if (i >> 24 != 0) {
				f += method_24099(i >> 24);
				g += method_24099(i >> 16);
				h += method_24099(i >> 8);
				m += method_24099(i >> 0);
			}

			if (j >> 24 != 0) {
				f += method_24099(j >> 24);
				g += method_24099(j >> 16);
				h += method_24099(j >> 8);
				m += method_24099(j >> 0);
			}

			if (k >> 24 != 0) {
				f += method_24099(k >> 24);
				g += method_24099(k >> 16);
				h += method_24099(k >> 8);
				m += method_24099(k >> 0);
			}

			if (l >> 24 != 0) {
				f += method_24099(l >> 24);
				g += method_24099(l >> 16);
				h += method_24099(l >> 8);
				m += method_24099(l >> 0);
			}

			f /= 4.0F;
			g /= 4.0F;
			h /= 4.0F;
			m /= 4.0F;
			int n = (int)(Math.pow((double)f, 0.45454545454545453) * 255.0);
			int o = (int)(Math.pow((double)g, 0.45454545454545453) * 255.0);
			int p = (int)(Math.pow((double)h, 0.45454545454545453) * 255.0);
			int q = (int)(Math.pow((double)m, 0.45454545454545453) * 255.0);
			if (n < 96) {
				n = 0;
			}

			return n << 24 | o << 16 | p << 8 | q;
		} else {
			int r = method_24100(i, j, k, l, 24);
			int s = method_24100(i, j, k, l, 16);
			int t = method_24100(i, j, k, l, 8);
			int u = method_24100(i, j, k, l, 0);
			return r << 24 | s << 16 | t << 8 | u;
		}
	}

	private static int method_24100(int i, int j, int k, int l, int m) {
		float f = method_24099(i >> m);
		float g = method_24099(j >> m);
		float h = method_24099(k >> m);
		float n = method_24099(l >> m);
		float o = (float)((double)((float)Math.pow((double)(f + g + h + n) * 0.25, 0.45454545454545453)));
		return (int)((double)o * 255.0);
	}

	private static float method_24099(int i) {
		return field_21747[i & 0xFF];
	}
}
