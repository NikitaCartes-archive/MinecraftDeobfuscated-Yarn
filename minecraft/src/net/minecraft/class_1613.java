package net.minecraft;

public class class_1613 extends class_1547 {
	public class_1613(class_1937 arg) {
		super(class_1299.field_6137, arg);
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_15200;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15069;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14877;
	}

	@Override
	class_3414 method_6998() {
		return class_3417.field_14548;
	}

	@Override
	protected void method_6099(class_1282 arg, int i, boolean bl) {
		super.method_6099(arg, i, bl);
		class_1297 lv = arg.method_5529();
		if (lv instanceof class_1548) {
			class_1548 lv2 = (class_1548)lv;
			if (lv2.method_7008()) {
				lv2.method_7002();
				this.method_5706(class_1802.field_8398);
			}
		}
	}

	@Override
	protected class_1665 method_6996(float f) {
		class_1799 lv = this.method_6118(class_1304.field_6171);
		if (lv.method_7909() == class_1802.field_8236) {
			class_1679 lv2 = new class_1679(this.field_6002, this);
			lv2.method_7435(this, f);
			return lv2;
		} else {
			class_1665 lv3 = super.method_6996(f);
			if (lv.method_7909() == class_1802.field_8087 && lv3 instanceof class_1667) {
				((class_1667)lv3).method_7459(lv);
			}

			return lv3;
		}
	}
}
