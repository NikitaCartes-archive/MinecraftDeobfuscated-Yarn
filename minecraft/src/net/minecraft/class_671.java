package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_671 extends class_4003 {
	private final class_4002 field_17801;

	private class_671(class_1937 arg, double d, double e, double f, double g, double h, double i, class_2390 arg2, class_4002 arg3) {
		super(arg, d, e, f, g, h, i);
		this.field_17801 = arg3;
		this.field_3852 *= 0.1F;
		this.field_3869 *= 0.1F;
		this.field_3850 *= 0.1F;
		float j = (float)Math.random() * 0.4F + 0.6F;
		this.field_3861 = ((float)(Math.random() * 0.2F) + 0.8F) * arg2.method_10285() * j;
		this.field_3842 = ((float)(Math.random() * 0.2F) + 0.8F) * arg2.method_10286() * j;
		this.field_3859 = ((float)(Math.random() * 0.2F) + 0.8F) * arg2.method_10284() * j;
		this.field_17867 = this.field_17867 * 0.75F * arg2.method_10283();
		int k = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.field_3847 = (int)Math.max((float)k * arg2.method_10283(), 1.0F);
		this.method_18142(arg3);
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
			this.method_18142(this.field_17801);
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
	public static class class_672 implements class_707<class_2390> {
		private final class_4002 field_17802;

		public class_672(class_4002 arg) {
			this.field_17802 = arg;
		}

		public class_703 method_3022(class_2390 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_671(arg2, d, e, f, g, h, i, arg, this.field_17802);
		}
	}
}
