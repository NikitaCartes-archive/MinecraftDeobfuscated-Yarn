package net.minecraft;

public class class_1412 extends class_1408 {
	private boolean field_6689;

	public class_1412(class_1308 arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected class_13 method_6336(int i) {
		this.field_6689 = this.field_6684 instanceof class_1433;
		this.field_6678 = new class_12(this.field_6689);
		return new class_13(this.field_6678, i);
	}

	@Override
	protected boolean method_6358() {
		return this.field_6689 || this.method_6351();
	}

	@Override
	protected class_243 method_6347() {
		return new class_243(this.field_6684.field_5987, this.field_6684.field_6010 + (double)this.field_6684.method_17682() * 0.5, this.field_6684.field_6035);
	}

	@Override
	public void method_6360() {
		this.field_6675++;
		if (this.field_6679) {
			this.method_6356();
		}

		if (!this.method_6357()) {
			if (this.method_6358()) {
				this.method_6339();
			} else if (this.field_6681 != null && this.field_6681.method_39() < this.field_6681.method_38()) {
				class_243 lv = this.field_6681.method_47(this.field_6684, this.field_6681.method_39());
				if (class_3532.method_15357(this.field_6684.field_5987) == class_3532.method_15357(lv.field_1352)
					&& class_3532.method_15357(this.field_6684.field_6010) == class_3532.method_15357(lv.field_1351)
					&& class_3532.method_15357(this.field_6684.field_6035) == class_3532.method_15357(lv.field_1350)) {
					this.field_6681.method_42(this.field_6681.method_39() + 1);
				}
			}

			class_4209.method_19470(this.field_6677, this.field_6684, this.field_6681, this.field_6683);
			if (!this.method_6357()) {
				class_243 lv = this.field_6681.method_49(this.field_6684);
				this.field_6684.method_5962().method_6239(lv.field_1352, lv.field_1351, lv.field_1350, this.field_6668);
			}
		}
	}

	@Override
	protected void method_6339() {
		if (this.field_6681 != null) {
			class_243 lv = this.method_6347();
			float f = this.field_6684.method_17681();
			float g = f > 0.75F ? f / 2.0F : 0.75F - f / 2.0F;
			class_243 lv2 = this.field_6684.method_18798();
			if (Math.abs(lv2.field_1352) > 0.2 || Math.abs(lv2.field_1350) > 0.2) {
				g = (float)((double)g * lv2.method_1033() * 6.0);
			}

			int i = 6;
			class_243 lv3 = this.field_6681.method_35();
			if (Math.abs(this.field_6684.field_5987 - (lv3.field_1352 + 0.5)) < (double)g
				&& Math.abs(this.field_6684.field_6035 - (lv3.field_1350 + 0.5)) < (double)g
				&& Math.abs(this.field_6684.field_6010 - lv3.field_1351) < (double)(g * 2.0F)) {
				this.field_6681.method_44();
			}

			for (int j = Math.min(this.field_6681.method_39() + 6, this.field_6681.method_38() - 1); j > this.field_6681.method_39(); j--) {
				lv3 = this.field_6681.method_47(this.field_6684, j);
				if (!(lv3.method_1025(lv) > 36.0) && this.method_6341(lv, lv3, 0, 0, 0)) {
					this.field_6681.method_42(j);
					break;
				}
			}

			this.method_6346(lv);
		}
	}

	@Override
	protected void method_6346(class_243 arg) {
		if (this.field_6675 - this.field_6674 > 100) {
			if (arg.method_1025(this.field_6672) < 2.25) {
				this.method_6340();
			}

			this.field_6674 = this.field_6675;
			this.field_6672 = arg;
		}

		if (this.field_6681 != null && !this.field_6681.method_46()) {
			class_243 lv = this.field_6681.method_35();
			if (lv.equals(this.field_6680)) {
				this.field_6670 = this.field_6670 + (class_156.method_658() - this.field_6669);
			} else {
				this.field_6680 = lv;
				double d = arg.method_1022(this.field_6680);
				this.field_6682 = this.field_6684.method_6029() > 0.0F ? d / (double)this.field_6684.method_6029() * 100.0 : 0.0;
			}

			if (this.field_6682 > 0.0 && (double)this.field_6670 > this.field_6682 * 2.0) {
				this.field_6680 = class_243.field_1353;
				this.field_6670 = 0L;
				this.field_6682 = 0.0;
				this.method_6340();
			}

			this.field_6669 = class_156.method_658();
		}
	}

	@Override
	protected boolean method_6341(class_243 arg, class_243 arg2, int i, int j, int k) {
		class_243 lv = new class_243(arg2.field_1352, arg2.field_1351 + (double)this.field_6684.method_17682() * 0.5, arg2.field_1350);
		return this.field_6677
				.method_17742(new class_3959(arg, lv, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, this.field_6684))
				.method_17783()
			== class_239.class_240.field_1333;
	}

	@Override
	public boolean method_6333(class_2338 arg) {
		return !this.field_6677.method_8320(arg).method_11598(this.field_6677, arg);
	}

	@Override
	public void method_6354(boolean bl) {
	}
}
