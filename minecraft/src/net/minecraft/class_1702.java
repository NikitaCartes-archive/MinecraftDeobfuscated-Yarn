package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1702 {
	private int field_7756 = 20;
	private float field_7753;
	private float field_7752;
	private int field_7755;
	private int field_7754 = 20;

	public class_1702() {
		this.field_7753 = 5.0F;
	}

	public void method_7585(int i, float f) {
		this.field_7756 = Math.min(i + this.field_7756, 20);
		this.field_7753 = Math.min(this.field_7753 + (float)i * f * 2.0F, (float)this.field_7756);
	}

	public void method_7579(class_1792 arg, class_1799 arg2) {
		if (arg.method_19263()) {
			class_4174 lv = arg.method_19264();
			this.method_7585(lv.method_19230(), lv.method_19231());
		}
	}

	public void method_7588(class_1657 arg) {
		class_1267 lv = arg.field_6002.method_8407();
		this.field_7754 = this.field_7756;
		if (this.field_7752 > 4.0F) {
			this.field_7752 -= 4.0F;
			if (this.field_7753 > 0.0F) {
				this.field_7753 = Math.max(this.field_7753 - 1.0F, 0.0F);
			} else if (lv != class_1267.field_5801) {
				this.field_7756 = Math.max(this.field_7756 - 1, 0);
			}
		}

		boolean bl = arg.field_6002.method_8450().method_8355(class_1928.field_19395);
		if (bl && this.field_7753 > 0.0F && arg.method_7317() && this.field_7756 >= 20) {
			this.field_7755++;
			if (this.field_7755 >= 10) {
				float f = Math.min(this.field_7753, 6.0F);
				arg.method_6025(f / 6.0F);
				this.method_7583(f);
				this.field_7755 = 0;
			}
		} else if (bl && this.field_7756 >= 18 && arg.method_7317()) {
			this.field_7755++;
			if (this.field_7755 >= 80) {
				arg.method_6025(1.0F);
				this.method_7583(6.0F);
				this.field_7755 = 0;
			}
		} else if (this.field_7756 <= 0) {
			this.field_7755++;
			if (this.field_7755 >= 80) {
				if (arg.method_6032() > 10.0F || lv == class_1267.field_5807 || arg.method_6032() > 1.0F && lv == class_1267.field_5802) {
					arg.method_5643(class_1282.field_5852, 1.0F);
				}

				this.field_7755 = 0;
			}
		} else {
			this.field_7755 = 0;
		}
	}

	public void method_7584(class_2487 arg) {
		if (arg.method_10573("foodLevel", 99)) {
			this.field_7756 = arg.method_10550("foodLevel");
			this.field_7755 = arg.method_10550("foodTickTimer");
			this.field_7753 = arg.method_10583("foodSaturationLevel");
			this.field_7752 = arg.method_10583("foodExhaustionLevel");
		}
	}

	public void method_7582(class_2487 arg) {
		arg.method_10569("foodLevel", this.field_7756);
		arg.method_10569("foodTickTimer", this.field_7755);
		arg.method_10548("foodSaturationLevel", this.field_7753);
		arg.method_10548("foodExhaustionLevel", this.field_7752);
	}

	public int method_7586() {
		return this.field_7756;
	}

	public boolean method_7587() {
		return this.field_7756 < 20;
	}

	public void method_7583(float f) {
		this.field_7752 = Math.min(this.field_7752 + f, 40.0F);
	}

	public float method_7589() {
		return this.field_7753;
	}

	public void method_7580(int i) {
		this.field_7756 = i;
	}

	@Environment(EnvType.CLIENT)
	public void method_7581(float f) {
		this.field_7753 = f;
	}
}
