package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_733 extends class_703 {
	private final class_1297 field_3894;
	private int field_3896;
	private final int field_3895;
	private final class_2394 field_3893;

	public class_733(class_1937 arg, class_1297 arg2, class_2394 arg3) {
		this(arg, arg2, arg3, 3);
	}

	public class_733(class_1937 arg, class_1297 arg2, class_2394 arg3, int i) {
		super(
			arg, arg2.field_5987, arg2.method_5829().field_1322 + (double)(arg2.field_6019 / 2.0F), arg2.field_6035, arg2.field_5967, arg2.field_5984, arg2.field_6006
		);
		this.field_3894 = arg2;
		this.field_3895 = i;
		this.field_3893 = arg3;
		this.method_3070();
	}

	@Override
	public void method_3074(class_287 arg, class_1297 arg2, float f, float g, float h, float i, float j, float k) {
	}

	@Override
	public void method_3070() {
		for (int i = 0; i < 16; i++) {
			double d = (double)(this.field_3840.nextFloat() * 2.0F - 1.0F);
			double e = (double)(this.field_3840.nextFloat() * 2.0F - 1.0F);
			double f = (double)(this.field_3840.nextFloat() * 2.0F - 1.0F);
			if (!(d * d + e * e + f * f > 1.0)) {
				double g = this.field_3894.field_5987 + d * (double)this.field_3894.field_5998 / 4.0;
				double h = this.field_3894.method_5829().field_1322 + (double)(this.field_3894.field_6019 / 2.0F) + e * (double)this.field_3894.field_6019 / 4.0;
				double j = this.field_3894.field_6035 + f * (double)this.field_3894.field_5998 / 4.0;
				this.field_3851.method_8466(this.field_3893, false, g, h, j, d, e + 0.2, f);
			}
		}

		this.field_3896++;
		if (this.field_3896 >= this.field_3895) {
			this.method_3085();
		}
	}

	@Override
	public int method_3079() {
		return 3;
	}
}
