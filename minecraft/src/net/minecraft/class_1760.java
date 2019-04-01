package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1760 extends class_1792 {
	public class_1760(class_1792.class_1793 arg) {
		super(arg);
		this.method_7863(new class_2960("time"), new class_1800() {
			@Environment(EnvType.CLIENT)
			private double field_7911;
			@Environment(EnvType.CLIENT)
			private double field_7910;
			@Environment(EnvType.CLIENT)
			private long field_7913;

			@Environment(EnvType.CLIENT)
			@Override
			public float call(class_1799 arg, @Nullable class_1937 arg2, @Nullable class_1309 arg3) {
				boolean bl = arg3 != null;
				class_1297 lv = (class_1297)(bl ? arg3 : arg.method_7945());
				if (arg2 == null && lv != null) {
					arg2 = lv.field_6002;
				}

				if (arg2 == null) {
					return 0.0F;
				} else {
					double d;
					if (arg2.field_9247.method_12462()) {
						d = (double)arg2.method_8400(1.0F);
					} else {
						d = Math.random();
					}

					d = this.method_7736(arg2, d);
					return (float)d;
				}
			}

			@Environment(EnvType.CLIENT)
			private double method_7736(class_1937 arg, double d) {
				if (arg.method_8510() != this.field_7913) {
					this.field_7913 = arg.method_8510();
					double e = d - this.field_7911;
					e = class_3532.method_15367(e + 0.5, 1.0) - 0.5;
					this.field_7910 += e * 0.1;
					this.field_7910 *= 0.9;
					this.field_7911 = class_3532.method_15367(this.field_7911 + this.field_7910, 1.0);
				}

				return this.field_7911;
			}
		});
	}
}
