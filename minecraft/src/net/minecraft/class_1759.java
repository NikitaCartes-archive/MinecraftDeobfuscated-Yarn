package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1759 extends class_1792 {
	public class_1759(class_1792.class_1793 arg) {
		super(arg);
		this.method_7863(new class_2960("angle"), new class_1800() {
			@Environment(EnvType.CLIENT)
			private double field_7907;
			@Environment(EnvType.CLIENT)
			private double field_7906;
			@Environment(EnvType.CLIENT)
			private long field_7908;

			@Environment(EnvType.CLIENT)
			@Override
			public float call(class_1799 arg, @Nullable class_1937 arg2, @Nullable class_1309 arg3) {
				if (arg3 == null && !arg.method_7961()) {
					return 0.0F;
				} else {
					boolean bl = arg3 != null;
					class_1297 lv = (class_1297)(bl ? arg3 : arg.method_7945());
					if (arg2 == null) {
						arg2 = lv.field_6002;
					}

					double f;
					if (arg2.field_9247.method_12462()) {
						double d = bl ? (double)lv.field_6031 : this.method_7733((class_1533)lv);
						d = class_3532.method_15367(d / 360.0, 1.0);
						double e = this.method_7734(arg2, lv) / (float) (Math.PI * 2);
						f = 0.5 - (d - 0.25 - e);
					} else {
						f = Math.random();
					}

					if (bl) {
						f = this.method_7735(arg2, f);
					}

					return class_3532.method_15341((float)f, 1.0F);
				}
			}

			@Environment(EnvType.CLIENT)
			private double method_7735(class_1937 arg, double d) {
				if (arg.method_8510() != this.field_7908) {
					this.field_7908 = arg.method_8510();
					double e = d - this.field_7907;
					e = class_3532.method_15367(e + 0.5, 1.0) - 0.5;
					this.field_7906 += e * 0.1;
					this.field_7906 *= 0.8;
					this.field_7907 = class_3532.method_15367(this.field_7907 + this.field_7906, 1.0);
				}

				return this.field_7907;
			}

			@Environment(EnvType.CLIENT)
			private double method_7733(class_1533 arg) {
				return (double)class_3532.method_15392(180 + arg.method_5735().method_10161() * 90);
			}

			@Environment(EnvType.CLIENT)
			private double method_7734(class_1936 arg, class_1297 arg2) {
				class_2338 lv = arg.method_8395();
				return Math.atan2((double)lv.method_10260() - arg2.field_6035, (double)lv.method_10263() - arg2.field_5987);
			}
		});
	}
}
