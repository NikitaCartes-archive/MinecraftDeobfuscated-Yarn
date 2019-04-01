package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_673 extends class_4003 {
	private final class_4002 field_17806;

	protected class_673(class_1937 arg, double d, double e, double f, double g, double h, double i, class_4002 arg2) {
		super(arg, d, e, f);
		this.field_17806 = arg2;
		this.field_3852 = g + (Math.random() * 2.0 - 1.0) * 0.05F;
		this.field_3869 = h + (Math.random() * 2.0 - 1.0) * 0.05F;
		this.field_3850 = i + (Math.random() * 2.0 - 1.0) * 0.05F;
		float j = this.field_3840.nextFloat() * 0.3F + 0.7F;
		this.field_3861 = j;
		this.field_3842 = j;
		this.field_3859 = j;
		this.field_17867 = 0.1F * (this.field_3840.nextFloat() * this.field_3840.nextFloat() * 6.0F + 1.0F);
		this.field_3847 = (int)(16.0 / ((double)this.field_3840.nextFloat() * 0.8 + 0.2)) + 2;
		this.method_18142(arg2);
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17828;
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		} else {
			this.method_18142(this.field_17806);
			this.field_3869 += 0.004;
			this.method_3069(this.field_3852, this.field_3869, this.field_3850);
			this.field_3852 *= 0.9F;
			this.field_3869 *= 0.9F;
			this.field_3850 *= 0.9F;
			if (this.field_3845) {
				this.field_3852 *= 0.7F;
				this.field_3850 *= 0.7F;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_674 implements class_707<class_2400> {
		private final class_4002 field_17807;

		public class_674(class_4002 arg) {
			this.field_17807 = arg;
		}

		public class_703 method_3023(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_673(arg2, d, e, f, g, h, i, this.field_17807);
		}
	}
}
