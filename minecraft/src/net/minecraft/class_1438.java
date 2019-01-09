package net.minecraft;

public class class_1438 extends class_1430 {
	public class_1438(class_1937 arg) {
		super(class_1299.field_6143, arg);
		this.method_5835(0.9F, 1.4F);
		this.field_6746 = class_2246.field_10402;
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		if (lv.method_7909() == class_1802.field_8428 && this.method_5618() >= 0 && !arg.field_7503.field_7477) {
			lv.method_7934(1);
			if (lv.method_7960()) {
				arg.method_6122(arg2, new class_1799(class_1802.field_8208));
			} else if (!arg.field_7514.method_7394(new class_1799(class_1802.field_8208))) {
				arg.method_7328(new class_1799(class_1802.field_8208), false);
			}

			return true;
		} else if (lv.method_7909() == class_1802.field_8868 && this.method_5618() >= 0) {
			this.field_6002.method_8406(class_2398.field_11236, this.field_5987, this.field_6010 + (double)(this.field_6019 / 2.0F), this.field_6035, 0.0, 0.0, 0.0);
			if (!this.field_6002.field_9236) {
				this.method_5650();
				class_1430 lv2 = new class_1430(this.field_6002);
				lv2.method_5808(this.field_5987, this.field_6010, this.field_6035, this.field_6031, this.field_5965);
				lv2.method_6033(this.method_6032());
				lv2.field_6283 = this.field_6283;
				if (this.method_16914()) {
					lv2.method_5665(this.method_5797());
				}

				this.field_6002.method_8649(lv2);

				for (int i = 0; i < 5; i++) {
					this.field_6002
						.method_8649(
							new class_1542(this.field_6002, this.field_5987, this.field_6010 + (double)this.field_6019, this.field_6035, new class_1799(class_2246.field_10559))
						);
				}

				lv.method_7956(1, arg);
				this.method_5783(class_3417.field_14705, 1.0F, 1.0F);
			}

			return true;
		} else {
			return super.method_5992(arg, arg2);
		}
	}

	public class_1438 method_6495(class_1296 arg) {
		return new class_1438(this.field_6002);
	}
}
