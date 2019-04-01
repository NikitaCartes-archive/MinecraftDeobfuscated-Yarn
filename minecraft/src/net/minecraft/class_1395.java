package net.minecraft;

import javax.annotation.Nullable;

public class class_1395 extends class_1394 {
	public class_1395(class_1314 arg, double d) {
		super(arg, d);
	}

	@Nullable
	@Override
	protected class_243 method_6302() {
		class_243 lv = null;
		if (this.field_6566.method_5799()) {
			lv = class_1414.method_6378(this.field_6566, 15, 15);
		}

		if (this.field_6566.method_6051().nextFloat() >= this.field_6626) {
			lv = this.method_6314();
		}

		return lv == null ? super.method_6302() : lv;
	}

	@Nullable
	private class_243 method_6314() {
		class_2338 lv = new class_2338(this.field_6566);
		class_2338.class_2339 lv2 = new class_2338.class_2339();
		class_2338.class_2339 lv3 = new class_2338.class_2339();

		for (class_2338 lv4 : class_2338.method_10094(
			class_3532.method_15357(this.field_6566.field_5987 - 3.0),
			class_3532.method_15357(this.field_6566.field_6010 - 6.0),
			class_3532.method_15357(this.field_6566.field_6035 - 3.0),
			class_3532.method_15357(this.field_6566.field_5987 + 3.0),
			class_3532.method_15357(this.field_6566.field_6010 + 6.0),
			class_3532.method_15357(this.field_6566.field_6035 + 3.0)
		)) {
			if (!lv.equals(lv4)) {
				class_2248 lv5 = this.field_6566.field_6002.method_8320(lv3.method_10101(lv4).method_10098(class_2350.field_11033)).method_11614();
				boolean bl = lv5 instanceof class_2397 || lv5.method_9525(class_3481.field_15475);
				if (bl && this.field_6566.field_6002.method_8623(lv4) && this.field_6566.field_6002.method_8623(lv2.method_10101(lv4).method_10098(class_2350.field_11036))
					)
				 {
					return new class_243((double)lv4.method_10263(), (double)lv4.method_10264(), (double)lv4.method_10260());
				}
			}
		}

		return null;
	}
}
