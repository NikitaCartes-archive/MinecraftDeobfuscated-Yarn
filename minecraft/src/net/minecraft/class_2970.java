package net.minecraft;

public class class_2970 extends class_2969 {
	@Override
	protected class_1799 method_10135(class_2342 arg, class_1799 arg2) {
		this.field_13364 = false;
		class_1792 lv = arg2.method_7909();
		if (lv instanceof class_1747) {
			class_2350 lv2 = arg.method_10120().method_11654(class_2315.field_10918);
			class_2338 lv3 = arg.method_10122().method_10093(lv2);
			class_2350 lv4 = arg.method_10207().method_8623(lv3.method_10074()) ? lv2 : class_2350.field_11036;
			this.field_13364 = ((class_1747)lv).method_7712(new class_2968(arg.method_10207(), lv3, lv2, arg2, lv4)) == class_1269.field_5812;
			if (this.field_13364) {
				arg2.method_7934(1);
			}
		}

		return arg2;
	}
}
