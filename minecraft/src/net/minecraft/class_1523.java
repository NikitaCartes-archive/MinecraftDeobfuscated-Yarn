package net.minecraft;

public class class_1523 extends class_1514 {
	private int field_7053;
	private int field_7052;
	private class_1295 field_7051;

	public class_1523(class_1510 arg) {
		super(arg);
	}

	@Override
	public void method_6853() {
		this.field_7053++;
		if (this.field_7053 % 2 == 0 && this.field_7053 < 10) {
			class_243 lv = this.field_7036.method_6834(1.0F).method_1029();
			lv.method_1024((float) (-Math.PI / 4));
			double d = this.field_7036.field_7017.field_5987;
			double e = this.field_7036.field_7017.field_6010 + (double)(this.field_7036.field_7017.field_6019 / 2.0F);
			double f = this.field_7036.field_7017.field_6035;

			for (int i = 0; i < 8; i++) {
				double g = d + this.field_7036.method_6051().nextGaussian() / 2.0;
				double h = e + this.field_7036.method_6051().nextGaussian() / 2.0;
				double j = f + this.field_7036.method_6051().nextGaussian() / 2.0;

				for (int k = 0; k < 6; k++) {
					this.field_7036
						.field_6002
						.method_8406(class_2398.field_11216, g, h, j, -lv.field_1352 * 0.08F * (double)k, -lv.field_1351 * 0.6F, -lv.field_1350 * 0.08F * (double)k);
				}

				lv.method_1024((float) (Math.PI / 16));
			}
		}
	}

	@Override
	public void method_6855() {
		this.field_7053++;
		if (this.field_7053 >= 200) {
			if (this.field_7052 >= 4) {
				this.field_7036.method_6831().method_6863(class_1527.field_7077);
			} else {
				this.field_7036.method_6831().method_6863(class_1527.field_7081);
			}
		} else if (this.field_7053 == 10) {
			class_243 lv = new class_243(
					this.field_7036.field_7017.field_5987 - this.field_7036.field_5987, 0.0, this.field_7036.field_7017.field_6035 - this.field_7036.field_6035
				)
				.method_1029();
			float f = 5.0F;
			double d = this.field_7036.field_7017.field_5987 + lv.field_1352 * 5.0 / 2.0;
			double e = this.field_7036.field_7017.field_6035 + lv.field_1350 * 5.0 / 2.0;
			double g = this.field_7036.field_7017.field_6010 + (double)(this.field_7036.field_7017.field_6019 / 2.0F);
			class_2338.class_2339 lv2 = new class_2338.class_2339(class_3532.method_15357(d), class_3532.method_15357(g), class_3532.method_15357(e));

			while (this.field_7036.field_6002.method_8623(lv2)) {
				lv2.method_10103(class_3532.method_15357(d), class_3532.method_15357(--g), class_3532.method_15357(e));
			}

			g = (double)(class_3532.method_15357(g) + 1);
			this.field_7051 = new class_1295(this.field_7036.field_6002, d, g, e);
			this.field_7051.method_5607(this.field_7036);
			this.field_7051.method_5603(5.0F);
			this.field_7051.method_5604(200);
			this.field_7051.method_5608(class_2398.field_11216);
			this.field_7051.method_5610(new class_1293(class_1294.field_5921));
			this.field_7036.field_6002.method_8649(this.field_7051);
		}
	}

	@Override
	public void method_6856() {
		this.field_7053 = 0;
		this.field_7052++;
	}

	@Override
	public void method_6854() {
		if (this.field_7051 != null) {
			this.field_7051.method_5650();
			this.field_7051 = null;
		}
	}

	@Override
	public class_1527<class_1523> method_6849() {
		return class_1527.field_7072;
	}

	public void method_6857() {
		this.field_7052 = 0;
	}
}
