package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_372 implements class_368 {
	private final class_372.class_373 field_2225;
	private final String field_2224;
	private final String field_2226;
	private class_368.class_369 field_2227 = class_368.class_369.field_2210;
	private long field_2223;
	private float field_2229;
	private float field_2228;
	private final boolean field_2222;

	public class_372(class_372.class_373 arg, class_2561 arg2, @Nullable class_2561 arg3, boolean bl) {
		this.field_2225 = arg;
		this.field_2224 = arg2.method_10863();
		this.field_2226 = arg3 == null ? null : arg3.method_10863();
		this.field_2222 = bl;
	}

	@Override
	public class_368.class_369 method_1986(class_374 arg, long l) {
		arg.method_1995().method_1531().method_4618(field_2207);
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		arg.blit(0, 0, 0, 96, 160, 32);
		this.field_2225.method_1994(arg, 6, 6);
		if (this.field_2226 == null) {
			arg.method_1995().field_1772.method_1729(this.field_2224, 30.0F, 12.0F, -11534256);
		} else {
			arg.method_1995().field_1772.method_1729(this.field_2224, 30.0F, 7.0F, -11534256);
			arg.method_1995().field_1772.method_1729(this.field_2226, 30.0F, 18.0F, -16777216);
		}

		if (this.field_2222) {
			class_332.fill(3, 28, 157, 29, -1);
			float f = (float)class_3532.method_15390((double)this.field_2229, (double)this.field_2228, (double)((float)(l - this.field_2223) / 100.0F));
			int i;
			if (this.field_2228 >= this.field_2229) {
				i = -16755456;
			} else {
				i = -11206656;
			}

			class_332.fill(3, 28, (int)(3.0F + 154.0F * f), 29, i);
			this.field_2229 = f;
			this.field_2223 = l;
		}

		return this.field_2227;
	}

	public void method_1993() {
		this.field_2227 = class_368.class_369.field_2209;
	}

	public void method_1992(float f) {
		this.field_2228 = f;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_373 {
		field_2230(0, 0),
		field_2237(1, 0),
		field_2235(2, 0),
		field_2233(0, 1),
		field_2236(1, 1);

		private final int field_2232;
		private final int field_2231;

		private class_373(int j, int k) {
			this.field_2232 = j;
			this.field_2231 = k;
		}

		public void method_1994(class_332 arg, int i, int j) {
			GlStateManager.enableBlend();
			arg.blit(i, j, 176 + this.field_2232 * 20, this.field_2231 * 20, 20, 20);
			GlStateManager.enableBlend();
		}
	}
}
