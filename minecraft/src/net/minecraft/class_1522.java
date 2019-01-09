package net.minecraft;

public class class_1522 extends class_1514 {
	private int field_7050;

	public class_1522(class_1510 arg) {
		super(arg);
	}

	@Override
	public void method_6855() {
		this.field_7050++;
		class_1309 lv = this.field_7036.field_6002.method_8460(this.field_7036, 20.0, 10.0);
		if (lv != null) {
			if (this.field_7050 > 25) {
				this.field_7036.method_6831().method_6863(class_1527.field_7073);
			} else {
				class_243 lv2 = new class_243(lv.field_5987 - this.field_7036.field_5987, 0.0, lv.field_6035 - this.field_7036.field_6035).method_1029();
				class_243 lv3 = new class_243(
						(double)class_3532.method_15374(this.field_7036.field_6031 * (float) (Math.PI / 180.0)),
						0.0,
						(double)(-class_3532.method_15362(this.field_7036.field_6031 * (float) (Math.PI / 180.0)))
					)
					.method_1029();
				float f = (float)lv3.method_1026(lv2);
				float g = (float)(Math.acos((double)f) * 180.0F / (float)Math.PI) + 0.5F;
				if (g < 0.0F || g > 10.0F) {
					double d = lv.field_5987 - this.field_7036.field_7017.field_5987;
					double e = lv.field_6035 - this.field_7036.field_7017.field_6035;
					double h = class_3532.method_15350(
						class_3532.method_15338(180.0 - class_3532.method_15349(d, e) * 180.0F / (float)Math.PI - (double)this.field_7036.field_6031), -100.0, 100.0
					);
					this.field_7036.field_6267 *= 0.8F;
					float i = class_3532.method_15368(d * d + e * e) + 1.0F;
					float j = i;
					if (i > 40.0F) {
						i = 40.0F;
					}

					this.field_7036.field_6267 = (float)((double)this.field_7036.field_6267 + h * (double)(0.7F / i / j));
					this.field_7036.field_6031 = this.field_7036.field_6031 + this.field_7036.field_6267;
				}
			}
		} else if (this.field_7050 >= 100) {
			lv = this.field_7036.field_6002.method_8460(this.field_7036, 150.0, 150.0);
			this.field_7036.method_6831().method_6863(class_1527.field_7077);
			if (lv != null) {
				this.field_7036.method_6831().method_6863(class_1527.field_7078);
				this.field_7036.method_6831().method_6865(class_1527.field_7078).method_6840(new class_243(lv.field_5987, lv.field_6010, lv.field_6035));
			}
		}
	}

	@Override
	public void method_6856() {
		this.field_7050 = 0;
	}

	@Override
	public class_1527<class_1522> method_6849() {
		return class_1527.field_7081;
	}
}
