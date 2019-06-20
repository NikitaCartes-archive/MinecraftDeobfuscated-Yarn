package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_682 extends class_4003 {
	private final float field_3809;
	private final class_4002 field_17808;

	private class_682(class_1937 arg, double d, double e, double f, float g, float h, float i, class_4002 arg2) {
		super(arg, d, e, f);
		this.field_17808 = arg2;
		this.field_3861 = g;
		this.field_3842 = h;
		this.field_3859 = i;
		float j = 0.9F;
		this.field_17867 *= 0.67499995F;
		int k = (int)(32.0 / (Math.random() * 0.8 + 0.2));
		this.field_3847 = (int)Math.max((float)k * 0.9F, 1.0F);
		this.method_18142(arg2);
		this.field_3809 = ((float)Math.random() - 0.5F) * 0.1F;
		this.field_3839 = (float)Math.random() * (float) (Math.PI * 2);
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17828;
	}

	@Override
	public float method_18132(float f) {
		return this.field_17867 * class_3532.method_15363(((float)this.field_3866 + f) / (float)this.field_3847 * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		} else {
			this.method_18142(this.field_17808);
			this.field_3857 = this.field_3839;
			this.field_3839 = this.field_3839 + (float) Math.PI * this.field_3809 * 2.0F;
			if (this.field_3845) {
				this.field_3857 = this.field_3839 = 0.0F;
			}

			this.method_3069(this.field_3852, this.field_3869, this.field_3850);
			this.field_3869 -= 0.003F;
			this.field_3869 = Math.max(this.field_3869, -0.14F);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_683 implements class_707<class_2388> {
		private final class_4002 field_17809;

		public class_683(class_4002 arg) {
			this.field_17809 = arg;
		}

		@Nullable
		public class_703 method_3033(class_2388 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_2680 lv = arg.method_10278();
			if (!lv.method_11588() && lv.method_11610() == class_2464.field_11455) {
				return null;
			} else {
				int j = class_310.method_1551().method_1505().method_1691(lv, arg2, new class_2338(d, e, f));
				if (lv.method_11614() instanceof class_2346) {
					j = ((class_2346)lv.method_11614()).method_10130(lv);
				}

				float k = (float)(j >> 16 & 0xFF) / 255.0F;
				float l = (float)(j >> 8 & 0xFF) / 255.0F;
				float m = (float)(j & 0xFF) / 255.0F;
				return new class_682(arg2, d, e, f, k, l, m, this.field_17809);
			}
		}
	}
}
