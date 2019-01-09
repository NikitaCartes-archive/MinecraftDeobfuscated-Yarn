package net.minecraft;

public class class_1505 extends class_1352 {
	private final class_1506 field_7002;

	public class_1505(class_1506 arg) {
		this.field_7002 = arg;
	}

	@Override
	public boolean method_6264() {
		return this.field_7002.field_6002.method_8528(this.field_7002.field_5987, this.field_7002.field_6010, this.field_7002.field_6035, 10.0);
	}

	@Override
	public void method_6268() {
		class_1266 lv = this.field_7002.field_6002.method_8404(new class_2338(this.field_7002));
		this.field_7002.method_6813(false);
		this.field_7002.method_6766(true);
		this.field_7002.method_5614(0);
		this.field_7002
			.field_6002
			.method_8416(new class_1538(this.field_7002.field_6002, this.field_7002.field_5987, this.field_7002.field_6010, this.field_7002.field_6035, true));
		class_1613 lv2 = this.method_6811(lv, this.field_7002);
		lv2.method_5804(this.field_7002);

		for (int i = 0; i < 3; i++) {
			class_1496 lv3 = this.method_6810(lv);
			class_1613 lv4 = this.method_6811(lv, lv3);
			lv4.method_5804(lv3);
			lv3.method_5762(this.field_7002.method_6051().nextGaussian() * 0.5, 0.0, this.field_7002.method_6051().nextGaussian() * 0.5);
		}
	}

	private class_1496 method_6810(class_1266 arg) {
		class_1506 lv = new class_1506(this.field_7002.field_6002);
		lv.method_5943(this.field_7002.field_6002, arg, class_3730.field_16461, null, null);
		lv.method_5814(this.field_7002.field_5987, this.field_7002.field_6010, this.field_7002.field_6035);
		lv.field_6008 = 60;
		lv.method_5971();
		lv.method_6766(true);
		lv.method_5614(0);
		lv.field_6002.method_8649(lv);
		return lv;
	}

	private class_1613 method_6811(class_1266 arg, class_1496 arg2) {
		class_1613 lv = new class_1613(arg2.field_6002);
		lv.method_5943(arg2.field_6002, arg, class_3730.field_16461, null, null);
		lv.method_5814(arg2.field_5987, arg2.field_6010, arg2.field_6035);
		lv.field_6008 = 60;
		lv.method_5971();
		if (lv.method_6118(class_1304.field_6169).method_7960()) {
			lv.method_5673(class_1304.field_6169, new class_1799(class_1802.field_8743));
		}

		lv.method_5673(
			class_1304.field_6173,
			class_1890.method_8233(lv.method_6051(), lv.method_6047(), (int)(5.0F + arg.method_5458() * (float)lv.method_6051().nextInt(18)), false)
		);
		lv.method_5673(
			class_1304.field_6169,
			class_1890.method_8233(lv.method_6051(), lv.method_6118(class_1304.field_6169), (int)(5.0F + arg.method_5458() * (float)lv.method_6051().nextInt(18)), false)
		);
		lv.field_6002.method_8649(lv);
		return lv;
	}
}
