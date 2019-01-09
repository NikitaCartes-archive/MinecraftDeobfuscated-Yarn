package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1153 implements class_1155 {
	private static final class_2561 field_5638 = new class_2588("tutorial.punch_tree.title");
	private static final class_2561 field_5639 = new class_2588("tutorial.punch_tree.description", class_1156.method_4913("attack"));
	private final class_1156 field_5634;
	private class_372 field_5637;
	private int field_5636;
	private int field_5635;

	public class_1153(class_1156 arg) {
		this.field_5634 = arg;
	}

	@Override
	public void method_4899() {
		this.field_5636++;
		if (this.field_5634.method_4905() != class_1934.field_9215) {
			this.field_5634.method_4910(class_1157.field_5653);
		} else {
			if (this.field_5636 == 1) {
				class_746 lv = this.field_5634.method_4914().field_1724;
				if (lv != null) {
					if (lv.field_7514.method_7382(class_3489.field_15539)) {
						this.field_5634.method_4910(class_1157.field_5655);
						return;
					}

					if (class_1152.method_4896(lv)) {
						this.field_5634.method_4910(class_1157.field_5655);
						return;
					}
				}
			}

			if ((this.field_5636 >= 600 || this.field_5635 > 3) && this.field_5637 == null) {
				this.field_5637 = new class_372(class_372.class_373.field_2235, field_5638, field_5639, true);
				this.field_5634.method_4914().method_1566().method_1999(this.field_5637);
			}
		}
	}

	@Override
	public void method_4902() {
		if (this.field_5637 != null) {
			this.field_5637.method_1993();
			this.field_5637 = null;
		}
	}

	@Override
	public void method_4900(class_638 arg, class_2338 arg2, class_2680 arg3, float f) {
		boolean bl = arg3.method_11602(class_3481.field_15475);
		if (bl && f > 0.0F) {
			if (this.field_5637 != null) {
				this.field_5637.method_1992(f);
			}

			if (f >= 1.0F) {
				this.field_5634.method_4910(class_1157.field_5652);
			}
		} else if (this.field_5637 != null) {
			this.field_5637.method_1992(0.0F);
		} else if (bl) {
			this.field_5635++;
		}
	}

	@Override
	public void method_4897(class_1799 arg) {
		if (class_3489.field_15539.method_15141(arg.method_7909())) {
			this.field_5634.method_4910(class_1157.field_5655);
		}
	}
}
