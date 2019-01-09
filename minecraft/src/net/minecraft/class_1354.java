package net.minecraft;

public class class_1354 extends class_1367 {
	private final class_1646 field_6458;
	private boolean field_6459;
	private boolean field_6457;
	private int field_6456;

	public class_1354(class_1646 arg, double d) {
		super(arg, d, 16);
		this.field_6458 = arg;
	}

	@Override
	public boolean method_6264() {
		if (this.field_6518 <= 0) {
			if (!this.field_6458.field_6002.method_8450().method_8355("mobGriefing")) {
				return false;
			}

			this.field_6456 = -1;
			this.field_6459 = this.field_6458.method_7244();
			this.field_6457 = this.field_6458.method_7239();
		}

		return super.method_6264();
	}

	@Override
	public boolean method_6266() {
		return this.field_6456 >= 0 && super.method_6266();
	}

	@Override
	public void method_6268() {
		super.method_6268();
		this.field_6458
			.method_5988()
			.method_6230(
				(double)this.field_6512.method_10263() + 0.5,
				(double)(this.field_6512.method_10264() + 1),
				(double)this.field_6512.method_10260() + 0.5,
				10.0F,
				(float)this.field_6458.method_5978()
			);
		if (this.method_6295()) {
			class_1936 lv = this.field_6458.field_6002;
			class_2338 lv2 = this.field_6512.method_10084();
			class_2680 lv3 = lv.method_8320(lv2);
			class_2248 lv4 = lv3.method_11614();
			if (this.field_6456 == 0 && lv4 instanceof class_2302 && ((class_2302)lv4).method_9825(lv3)) {
				lv.method_8651(lv2, true);
			} else if (this.field_6456 == 1 && lv3.method_11588()) {
				class_1277 lv5 = this.field_6458.method_7242();

				for (int i = 0; i < lv5.method_5439(); i++) {
					class_1799 lv6 = lv5.method_5438(i);
					boolean bl = false;
					if (!lv6.method_7960()) {
						if (lv6.method_7909() == class_1802.field_8317) {
							lv.method_8652(lv2, class_2246.field_10293.method_9564(), 3);
							bl = true;
						} else if (lv6.method_7909() == class_1802.field_8567) {
							lv.method_8652(lv2, class_2246.field_10247.method_9564(), 3);
							bl = true;
						} else if (lv6.method_7909() == class_1802.field_8179) {
							lv.method_8652(lv2, class_2246.field_10609.method_9564(), 3);
							bl = true;
						} else if (lv6.method_7909() == class_1802.field_8309) {
							lv.method_8652(lv2, class_2246.field_10341.method_9564(), 3);
							bl = true;
						}
					}

					if (bl) {
						lv6.method_7934(1);
						if (lv6.method_7960()) {
							lv5.method_5447(i, class_1799.field_8037);
						}
						break;
					}
				}
			}

			this.field_6456 = -1;
			this.field_6518 = 10;
		}
	}

	@Override
	protected boolean method_6296(class_1941 arg, class_2338 arg2) {
		class_2248 lv = arg.method_8320(arg2).method_11614();
		if (lv == class_2246.field_10362) {
			arg2 = arg2.method_10084();
			class_2680 lv2 = arg.method_8320(arg2);
			lv = lv2.method_11614();
			if (lv instanceof class_2302 && ((class_2302)lv).method_9825(lv2) && this.field_6457 && (this.field_6456 == 0 || this.field_6456 < 0)) {
				this.field_6456 = 0;
				return true;
			}

			if (lv2.method_11588() && this.field_6459 && (this.field_6456 == 1 || this.field_6456 < 0)) {
				this.field_6456 = 1;
				return true;
			}
		}

		return false;
	}
}
