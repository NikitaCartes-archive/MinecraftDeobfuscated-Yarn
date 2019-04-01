package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_425 extends class_4071 {
	private static final class_2960 field_2483 = new class_2960("textures/gui/title/mojang.png");
	private final class_310 field_18217;
	private final class_4011 field_17767;
	private final Runnable field_18218;
	private final boolean field_18219;
	private float field_17770;
	private long field_17771 = -1L;
	private long field_18220 = -1L;

	public class_425(class_310 arg, class_4011 arg2, Runnable runnable, boolean bl) {
		this.field_18217 = arg;
		this.field_17767 = arg2;
		this.field_18218 = runnable;
		this.field_18219 = bl;
	}

	public static void method_18819(class_310 arg) {
		arg.method_1531().method_4616(field_2483, new class_425.class_4070(field_2483));
	}

	@Override
	public void render(int i, int j, float f) {
		int k = this.field_18217.field_1704.method_4486();
		int l = this.field_18217.field_1704.method_4502();
		long m = class_156.method_658();
		if (this.field_18219 && (this.field_17767.method_18786() || this.field_18217.field_1755 != null) && this.field_18220 == -1L) {
			this.field_18220 = m;
		}

		float g = this.field_17771 > -1L ? (float)(m - this.field_17771) / 1000.0F : -1.0F;
		float h = this.field_18220 > -1L ? (float)(m - this.field_18220) / 500.0F : -1.0F;
		float o;
		if (g >= 1.0F) {
			if (this.field_18217.field_1755 != null) {
				this.field_18217.field_1755.render(0, 0, f);
			}

			int n = class_3532.method_15386((1.0F - class_3532.method_15363(g - 1.0F, 0.0F, 1.0F)) * 255.0F);
			fill(0, 0, k, l, 16777215 | n << 24);
			o = 1.0F - class_3532.method_15363(g - 1.0F, 0.0F, 1.0F);
		} else if (this.field_18219) {
			if (this.field_18217.field_1755 != null && h < 1.0F) {
				this.field_18217.field_1755.render(i, j, f);
			}

			int n = class_3532.method_15384(class_3532.method_15350((double)h, 0.15, 1.0) * 255.0);
			fill(0, 0, k, l, 16777215 | n << 24);
			o = class_3532.method_15363(h, 0.0F, 1.0F);
		} else {
			fill(0, 0, k, l, -1);
			o = 1.0F;
		}

		int n = (this.field_18217.field_1704.method_4486() - 256) / 2;
		int p = (this.field_18217.field_1704.method_4502() - 256) / 2;
		this.field_18217.method_1531().method_4618(field_2483);
		GlStateManager.enableBlend();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, o);
		this.blit(n, p, 0, 0, 256, 256);
		float q = this.field_17767.method_18229();
		this.field_17770 = this.field_17770 * 0.95F + q * 0.050000012F;
		if (g < 1.0F) {
			this.method_18103(k / 2 - 150, l / 4 * 3, k / 2 + 150, l / 4 * 3 + 10, this.field_17770, 1.0F - class_3532.method_15363(g, 0.0F, 1.0F));
		}

		if (g >= 2.0F) {
			this.field_18217.method_18502(null);
		}

		if (this.field_17771 == -1L && this.field_17767.method_18787() && (!this.field_18219 || h >= 2.0F)) {
			this.field_17767.method_18849();
			this.field_17771 = class_156.method_658();
			this.field_18218.run();
			if (this.field_18217.field_1755 != null) {
				this.field_18217.field_1755.init(this.field_18217, this.field_18217.field_1704.method_4486(), this.field_18217.field_1704.method_4502());
			}
		}
	}

	private void method_18103(int i, int j, int k, int l, float f, float g) {
		int m = class_3532.method_15386((float)(k - i - 2) * f);
		fill(i - 1, j - 1, k + 1, l + 1, 0xFF000000 | Math.round((1.0F - g) * 255.0F) << 16 | Math.round((1.0F - g) * 255.0F) << 8 | Math.round((1.0F - g) * 255.0F));
		fill(i, j, k, l, -1);
		fill(
			i + 1,
			j + 1,
			i + m,
			l - 1,
			0xFF000000
				| (int)class_3532.method_16439(1.0F - g, 226.0F, 255.0F) << 16
				| (int)class_3532.method_16439(1.0F - g, 40.0F, 255.0F) << 8
				| (int)class_3532.method_16439(1.0F - g, 55.0F, 255.0F)
		);
	}

	@Override
	public boolean method_18640() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public static class class_4070 extends class_1049 {
		private final class_2960 field_19234;

		public class_4070(class_2960 arg) {
			super(arg);
			this.field_19234 = arg;
		}

		@Override
		protected class_1049.class_4006 method_18153(class_3300 arg) {
			class_310 lv = class_310.method_1551();
			class_3268 lv2 = lv.method_1516().method_4633();

			try {
				InputStream inputStream = lv2.method_14405(class_3264.field_14188, this.field_19234);
				Throwable var5 = null;

				class_1049.class_4006 var6;
				try {
					var6 = new class_1049.class_4006(null, class_1011.method_4309(inputStream));
				} catch (Throwable var16) {
					var5 = var16;
					throw var16;
				} finally {
					if (inputStream != null) {
						if (var5 != null) {
							try {
								inputStream.close();
							} catch (Throwable var15) {
								var5.addSuppressed(var15);
							}
						} else {
							inputStream.close();
						}
					}
				}

				return var6;
			} catch (IOException var18) {
				return new class_1049.class_4006(var18);
			}
		}
	}
}
