package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1142 implements class_3000 {
	private final Random field_5571 = new Random();
	private final class_310 field_5575;
	private class_1113 field_5574;
	private int field_5572 = 100;
	private boolean field_5573;

	public class_1142(class_310 arg) {
		this.field_5575 = arg;
	}

	@Override
	public void method_16896() {
		class_1142.class_1143 lv = this.field_5575.method_1544();
		if (this.field_5574 != null) {
			if (!lv.method_4861().method_14833().equals(this.field_5574.method_4775())) {
				this.field_5575.method_1483().method_4870(this.field_5574);
				this.field_5572 = class_3532.method_15395(this.field_5571, 0, lv.method_4863() / 2);
				this.field_5573 = false;
			}

			if (!this.field_5573 && !this.field_5575.method_1483().method_4877(this.field_5574)) {
				this.field_5574 = null;
				this.field_5572 = Math.min(class_3532.method_15395(this.field_5571, lv.method_4863(), lv.method_4862()), this.field_5572);
			} else if (this.field_5575.method_1483().method_4877(this.field_5574)) {
				this.field_5573 = false;
			}
		}

		this.field_5572 = Math.min(this.field_5572, lv.method_4862());
		if (this.field_5574 == null && this.field_5572-- <= 0) {
			this.method_4858(lv);
		}
	}

	public void method_4858(class_1142.class_1143 arg) {
		this.field_5574 = class_1109.method_4759(arg.method_4861());
		this.field_5575.method_1483().method_4873(this.field_5574);
		this.field_5572 = Integer.MAX_VALUE;
		this.field_5573 = true;
	}

	public void method_4859() {
		if (this.field_5574 != null) {
			this.field_5575.method_1483().method_4870(this.field_5574);
			this.field_5574 = null;
			this.field_5572 = 0;
			this.field_5573 = false;
		}
	}

	public boolean method_4860(class_1142.class_1143 arg) {
		return this.field_5574 == null ? false : arg.method_4861().method_14833().equals(this.field_5574.method_4775());
	}

	@Environment(EnvType.CLIENT)
	public static enum class_1143 {
		field_5585(class_3417.field_15129, 20, 600),
		field_5586(class_3417.field_14681, 12000, 24000),
		field_5581(class_3417.field_14995, 1200, 3600),
		field_5578(class_3417.field_14755, 0, 0),
		field_5582(class_3417.field_14893, 1200, 3600),
		field_5580(class_3417.field_14837, 0, 0),
		field_5583(class_3417.field_14631, 6000, 24000),
		field_5576(class_3417.field_15198, 12000, 24000);

		private final class_3414 field_5577;
		private final int field_5587;
		private final int field_5584;

		private class_1143(class_3414 arg, int j, int k) {
			this.field_5577 = arg;
			this.field_5587 = j;
			this.field_5584 = k;
		}

		public class_3414 method_4861() {
			return this.field_5577;
		}

		public int method_4863() {
			return this.field_5587;
		}

		public int method_4862() {
			return this.field_5584;
		}
	}
}
