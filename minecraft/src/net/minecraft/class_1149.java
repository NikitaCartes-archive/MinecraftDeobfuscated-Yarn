package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1149 implements class_1155 {
	private static final class_2561 field_5611 = new class_2588("tutorial.craft_planks.title");
	private static final class_2561 field_5612 = new class_2588("tutorial.craft_planks.description");
	private final class_1156 field_5608;
	private class_372 field_5610;
	private int field_5609;

	public class_1149(class_1156 arg) {
		this.field_5608 = arg;
	}

	@Override
	public void method_4899() {
		this.field_5609++;
		if (this.field_5608.method_4905() != class_1934.field_9215) {
			this.field_5608.method_4910(class_1157.field_5653);
		} else {
			if (this.field_5609 == 1) {
				class_746 lv = this.field_5608.method_4914().field_1724;
				if (lv != null) {
					if (lv.field_7514.method_7382(class_3489.field_15537)) {
						this.field_5608.method_4910(class_1157.field_5653);
						return;
					}

					if (method_4895(lv, class_3489.field_15537)) {
						this.field_5608.method_4910(class_1157.field_5653);
						return;
					}
				}
			}

			if (this.field_5609 >= 1200 && this.field_5610 == null) {
				this.field_5610 = new class_372(class_372.class_373.field_2236, field_5611, field_5612, false);
				this.field_5608.method_4914().method_1566().method_1999(this.field_5610);
			}
		}
	}

	@Override
	public void method_4902() {
		if (this.field_5610 != null) {
			this.field_5610.method_1993();
			this.field_5610 = null;
		}
	}

	@Override
	public void method_4897(class_1799 arg) {
		class_1792 lv = arg.method_7909();
		if (class_3489.field_15537.method_15141(lv)) {
			this.field_5608.method_4910(class_1157.field_5653);
		}
	}

	public static boolean method_4895(class_746 arg, class_3494<class_1792> arg2) {
		for (class_1792 lv : arg2.method_15138()) {
			if (arg.method_3143().method_15025(class_3468.field_15370.method_14956(lv)) > 0) {
				return true;
			}
		}

		return false;
	}
}
