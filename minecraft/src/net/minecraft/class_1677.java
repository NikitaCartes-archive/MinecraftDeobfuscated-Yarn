package net.minecraft;

public class class_1677 extends class_3855 {
	public class_1677(class_1937 arg) {
		super(class_1299.field_6049, arg, 0.3125F, 0.3125F);
	}

	public class_1677(class_1937 arg, class_1309 arg2, double d, double e, double f) {
		super(class_1299.field_6049, arg2, d, e, f, arg, 0.3125F, 0.3125F);
	}

	public class_1677(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(class_1299.field_6049, d, e, f, g, h, i, arg, 0.3125F, 0.3125F);
	}

	@Override
	protected void method_7469(class_239 arg) {
		if (!this.field_6002.field_9236) {
			if (arg.field_1326 != null) {
				if (!arg.field_1326.method_5753()) {
					arg.field_1326.method_5639(5);
					boolean bl = arg.field_1326.method_5643(class_1282.method_5521(this, this.field_7604), 5.0F);
					if (bl) {
						this.method_5723(this.field_7604, arg.field_1326);
					}
				}
			} else {
				boolean bl = true;
				if (this.field_7604 != null && this.field_7604 instanceof class_1308) {
					bl = this.field_6002.method_8450().method_8355("mobGriefing");
				}

				if (bl) {
					class_2338 lv = arg.method_1015().method_10093(arg.field_1327);
					if (this.field_6002.method_8623(lv)) {
						this.field_6002.method_8501(lv, class_2246.field_10036.method_9564());
					}
				}
			}

			this.method_5650();
		}
	}

	@Override
	public boolean method_5863() {
		return false;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		return false;
	}
}
