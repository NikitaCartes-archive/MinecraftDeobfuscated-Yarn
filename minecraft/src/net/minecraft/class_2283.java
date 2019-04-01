package net.minecraft;

import java.util.Random;

public class class_2283 extends class_2429 {
	protected class_2283(class_2248.class_2251 arg) {
		super(0.3125F, arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11332, Boolean.valueOf(false))
				.method_11657(field_11335, Boolean.valueOf(false))
				.method_11657(field_11331, Boolean.valueOf(false))
				.method_11657(field_11328, Boolean.valueOf(false))
				.method_11657(field_11327, Boolean.valueOf(false))
				.method_11657(field_11330, Boolean.valueOf(false))
		);
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9759(arg.method_8045(), arg.method_8037());
	}

	public class_2680 method_9759(class_1922 arg, class_2338 arg2) {
		class_2248 lv = arg.method_8320(arg2.method_10074()).method_11614();
		class_2248 lv2 = arg.method_8320(arg2.method_10084()).method_11614();
		class_2248 lv3 = arg.method_8320(arg2.method_10095()).method_11614();
		class_2248 lv4 = arg.method_8320(arg2.method_10078()).method_11614();
		class_2248 lv5 = arg.method_8320(arg2.method_10072()).method_11614();
		class_2248 lv6 = arg.method_8320(arg2.method_10067()).method_11614();
		return this.method_9564()
			.method_11657(field_11330, Boolean.valueOf(lv == this || lv == class_2246.field_10528 || lv == class_2246.field_10471))
			.method_11657(field_11327, Boolean.valueOf(lv2 == this || lv2 == class_2246.field_10528))
			.method_11657(field_11332, Boolean.valueOf(lv3 == this || lv3 == class_2246.field_10528))
			.method_11657(field_11335, Boolean.valueOf(lv4 == this || lv4 == class_2246.field_10528))
			.method_11657(field_11331, Boolean.valueOf(lv5 == this || lv5 == class_2246.field_10528))
			.method_11657(field_11328, Boolean.valueOf(lv6 == this || lv6 == class_2246.field_10528));
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (!arg.method_11591(arg4, arg5)) {
			arg4.method_8397().method_8676(arg5, this, 1);
			return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
		} else {
			class_2248 lv = arg3.method_11614();
			boolean bl = lv == this || lv == class_2246.field_10528 || arg2 == class_2350.field_11033 && lv == class_2246.field_10471;
			return arg.method_11657((class_2769)field_11329.get(arg2), Boolean.valueOf(bl));
		}
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!arg.method_11591(arg2, arg3)) {
			arg2.method_8651(arg3, true);
		}
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2680 lv = arg2.method_8320(arg3.method_10074());
		boolean bl = !arg2.method_8320(arg3.method_10084()).method_11588() && !lv.method_11588();

		for (class_2350 lv2 : class_2350.class_2353.field_11062) {
			class_2338 lv3 = arg3.method_10093(lv2);
			class_2248 lv4 = arg2.method_8320(lv3).method_11614();
			if (lv4 == this) {
				if (bl) {
					return false;
				}

				class_2248 lv5 = arg2.method_8320(lv3.method_10074()).method_11614();
				if (lv5 == this || lv5 == class_2246.field_10471) {
					return true;
				}
			}
		}

		class_2248 lv6 = lv.method_11614();
		return lv6 == this || lv6 == class_2246.field_10471;
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11332, field_11335, field_11331, field_11328, field_11327, field_11330);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
