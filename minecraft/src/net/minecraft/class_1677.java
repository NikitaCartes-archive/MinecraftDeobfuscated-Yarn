package net.minecraft;

public class class_1677 extends class_3855 {
	public class_1677(class_1299<? extends class_1677> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	public class_1677(class_1937 arg, class_1309 arg2, double d, double e, double f) {
		super(class_1299.field_6049, arg2, d, e, f, arg);
	}

	public class_1677(class_1937 arg, double d, double e, double f, double g, double h, double i) {
		super(class_1299.field_6049, d, e, f, g, h, i, arg);
	}

	@Override
	protected void method_7469(class_239 arg) {
		if (!this.field_6002.field_9236) {
			if (arg.method_17783() == class_239.class_240.field_1331) {
				class_1297 lv = ((class_3966)arg).method_17782();
				if (!lv.method_5753()) {
					int i = lv.method_20802();
					lv.method_5639(5);
					boolean bl = lv.method_5643(class_1282.method_5521(this, this.field_7604), 5.0F);
					if (bl) {
						this.method_5723(this.field_7604, lv);
					} else {
						lv.method_20803(i);
					}
				}
			} else if (this.field_7604 == null || !(this.field_7604 instanceof class_1308) || this.field_6002.method_8450().method_8355(class_1928.field_19388)) {
				class_3965 lv2 = (class_3965)arg;
				class_2338 lv3 = lv2.method_17777().method_10093(lv2.method_17780());
				if (this.field_6002.method_8623(lv3)) {
					this.field_6002.method_8501(lv3, class_2246.field_10036.method_9564());
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
