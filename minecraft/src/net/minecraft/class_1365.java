package net.minecraft;

public class class_1365 extends class_1352 {
	private final class_1314 field_6498;
	private class_1417 field_6496;
	private int field_6499 = -1;
	private int field_6497 = -1;

	public class_1365(class_1314 arg) {
		this.field_6498 = arg;
		this.method_6265(1);
	}

	@Override
	public boolean method_6264() {
		class_2338 lv = new class_2338(this.field_6498);
		if (this.field_6498 instanceof class_3758) {
			class_3758 lv2 = (class_3758)this.field_6498;
			class_3765 lv3 = lv2.method_16461();
			if (lv3 != null && lv3.method_16832()) {
				class_1415 lv4 = lv2.method_7232();
				this.field_6496 = lv4.method_6412(lv);
				if (lv4 != null && this.field_6496 != null && !this.method_16463()) {
					return true;
				}
			}
		}

		if ((
				!this.field_6498.field_6002.method_8530()
					|| this.field_6498.field_6002.method_8419() && this.field_6498.field_6002.method_8310(lv).method_8694() != class_1959.class_1963.field_9382
			)
			&& this.field_6498.field_6002.field_9247.method_12451()) {
			if (this.field_6498.method_6051().nextInt(50) != 0) {
				return false;
			} else if (this.method_16463()) {
				return false;
			} else {
				class_1415 lv5 = this.field_6498.field_6002.method_8557().method_6438(lv, 14);
				if (lv5 == null) {
					return false;
				} else {
					this.field_6496 = lv5.method_6412(lv);
					return this.field_6496 != null;
				}
			}
		} else {
			return false;
		}
	}

	private boolean method_16463() {
		return this.field_6499 != -1 && this.field_6498.method_5649((double)this.field_6499, this.field_6498.field_6010, (double)this.field_6497) < 4.0;
	}

	@Override
	public boolean method_6266() {
		return !this.field_6498.method_5942().method_6357();
	}

	@Override
	public void method_6269() {
		this.field_6499 = -1;
		class_2338 lv = this.field_6496.method_6422();
		int i = lv.method_10263();
		int j = lv.method_10264();
		int k = lv.method_10260();
		if (this.field_6498.method_5831(lv) > 256.0) {
			class_243 lv2 = class_1414.method_6373(this.field_6498, 14, 3, new class_243((double)i + 0.5, (double)j, (double)k + 0.5));
			if (lv2 != null) {
				this.field_6498.method_5942().method_6337(lv2.field_1352, lv2.field_1351, lv2.field_1350, 1.0);
			}
		} else {
			this.field_6498.method_5942().method_6337((double)i + 0.5, (double)j, (double)k + 0.5, 1.0);
		}
	}

	@Override
	public void method_6270() {
		this.field_6499 = this.field_6496.method_6422().method_10263();
		this.field_6497 = this.field_6496.method_6422().method_10260();
		this.field_6496 = null;
	}
}
