package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_717 extends class_4003 {
	private final class_4002 field_17868;

	protected class_717(class_1937 arg, double d, double e, double f, double g, double h, double i, float j, class_4002 arg2) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.field_17868 = arg2;
		this.field_3852 *= 0.1F;
		this.field_3869 *= 0.1F;
		this.field_3850 *= 0.1F;
		this.field_3852 += g;
		this.field_3869 += h;
		this.field_3850 += i;
		float k = (float)(Math.random() * 0.3F);
		this.field_3861 = k;
		this.field_3842 = k;
		this.field_3859 = k;
		this.field_17867 *= 0.75F * j;
		this.field_3847 = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.field_3847 = (int)((float)this.field_3847 * j);
		this.field_3847 = Math.max(this.field_3847, 1);
		this.method_18142(arg2);
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
			this.method_18142(this.field_17868);
			this.field_3869 += 0.004;
			this.method_3069(this.field_3852, this.field_3869, this.field_3850);
			if (this.field_3854 == this.field_3838) {
				this.field_3852 *= 1.1;
				this.field_3850 *= 1.1;
			}

			this.field_3852 *= 0.96F;
			this.field_3869 *= 0.96F;
			this.field_3850 *= 0.96F;
			if (this.field_3845) {
				this.field_3852 *= 0.7F;
				this.field_3850 *= 0.7F;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_718 implements class_707<class_2400> {
		private final class_4002 field_17869;

		public class_718(class_4002 arg) {
			this.field_17869 = arg;
		}

		public class_703 method_3101(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_717(arg2, d, e, f, g, h, i, 1.0F, this.field_17869);
		}
	}
}
