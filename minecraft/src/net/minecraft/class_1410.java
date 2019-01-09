package net.minecraft;

public class class_1410 extends class_1409 {
	private class_2338 field_6687;

	public class_1410(class_1308 arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	public class_11 method_6348(class_2338 arg) {
		this.field_6687 = arg;
		return super.method_6348(arg);
	}

	@Override
	public class_11 method_6349(class_1297 arg) {
		this.field_6687 = new class_2338(arg);
		return super.method_6349(arg);
	}

	@Override
	public boolean method_6335(class_1297 arg, double d) {
		class_11 lv = this.method_6349(arg);
		if (lv != null) {
			return this.method_6334(lv, d);
		} else {
			this.field_6687 = new class_2338(arg);
			this.field_6668 = d;
			return true;
		}
	}

	@Override
	public void method_6360() {
		if (!this.method_6357()) {
			super.method_6360();
		} else {
			if (this.field_6687 != null) {
				double d = (double)(this.field_6684.field_5998 * this.field_6684.field_5998);
				if (!(this.field_6684.method_5677(this.field_6687) < d)
					&& (
						!(this.field_6684.field_6010 > (double)this.field_6687.method_10264())
							|| !(
								this.field_6684
										.method_5677(new class_2338(this.field_6687.method_10263(), class_3532.method_15357(this.field_6684.field_6010), this.field_6687.method_10260()))
									< d
							)
					)) {
					this.field_6684
						.method_5962()
						.method_6239((double)this.field_6687.method_10263(), (double)this.field_6687.method_10264(), (double)this.field_6687.method_10260(), this.field_6668);
				} else {
					this.field_6687 = null;
				}
			}
		}
	}
}
