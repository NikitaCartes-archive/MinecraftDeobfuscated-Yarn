package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_661 extends class_3940 {
	protected class_661(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(arg, d, e, f, 0.0, 0.0, 0.0);
		this.field_3861 = 1.0F;
		this.field_3842 = 1.0F;
		this.field_3859 = 1.0F;
		this.method_3076(256);
		this.field_3847 = 4;
		this.field_3844 = 0.008F;
		this.field_3852 = g;
		this.field_3869 = h;
		this.field_3850 = i;
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		this.field_3869 = this.field_3869 - (double)this.field_3844;
		this.method_3069(this.field_3852, this.field_3869, this.field_3850);
		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		} else {
			int i = this.field_3866 * 5 / this.field_3847;
			if (i <= 4) {
				this.method_3076(256 + i);
			}
		}
	}

	@Override
	public void method_3076(int i) {
		if (this.method_3079() != 0) {
			throw new RuntimeException("Invalid call to Particle.setMiscTex");
		} else {
			this.field_3868 = 2 * i % 16;
			this.field_3848 = i / 16;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_662 implements class_707<class_2400> {
		@Nullable
		public class_703 method_3016(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_661(arg2, d, e, f, g, h, i);
		}
	}
}
