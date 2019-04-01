package net.minecraft;

public class class_1430 extends class_1429 {
	public class_1430(class_1299<? extends class_1430> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201.method_6277(1, new class_1374(this, 2.0));
		this.field_6201.method_6277(2, new class_1341(this, 1.0));
		this.field_6201.method_6277(3, new class_1391(this, 1.25, class_1856.method_8091(class_1802.field_8861), false));
		this.field_6201.method_6277(4, new class_1353(this, 1.25));
		this.field_6201.method_6277(5, new class_1394(this, 1.0));
		this.field_6201.method_6277(6, new class_1361(this, class_1657.class, 6.0F));
		this.field_6201.method_6277(7, new class_1376(this));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(10.0);
		this.method_5996(class_1612.field_7357).method_6192(0.2F);
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14780;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14597;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14857;
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		this.method_5783(class_3417.field_15110, 0.15F, 1.0F);
	}

	@Override
	protected float method_6107() {
		return 0.4F;
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		if (lv.method_7909() == class_1802.field_8550 && !arg.field_7503.field_7477 && !this.method_6109()) {
			arg.method_5783(class_3417.field_14691, 1.0F, 1.0F);
			lv.method_7934(1);
			if (lv.method_7960()) {
				arg.method_6122(arg2, new class_1799(class_1802.field_8103));
			} else if (!arg.field_7514.method_7394(new class_1799(class_1802.field_8103))) {
				arg.method_7328(new class_1799(class_1802.field_8103), false);
			}

			return true;
		} else {
			return super.method_5992(arg, arg2);
		}
	}

	public class_1430 method_6483(class_1296 arg) {
		return class_1299.field_6085.method_5883(this.field_6002);
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return this.method_6109() ? arg2.field_18068 : 1.3F;
	}
}
