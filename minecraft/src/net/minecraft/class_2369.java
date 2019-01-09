package net.minecraft;

import java.util.Random;

public class class_2369 extends class_2248 {
	protected static final class_265 field_11106 = class_2344.field_11010;

	protected class_2369(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public boolean method_9526(class_2680 arg) {
		return true;
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return !this.method_9564().method_11591(arg.method_8045(), arg.method_8037())
			? class_2248.method_9582(this.method_9564(), class_2246.field_10566.method_9564(), arg.method_8045(), arg.method_8037())
			: super.method_9605(arg);
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (arg2 == class_2350.field_11036 && !arg.method_11591(arg4, arg5)) {
			arg4.method_8397().method_8676(arg5, this, 1);
		}

		return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		class_2344.method_10125(arg, arg2, arg3);
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2680 lv = arg2.method_8320(arg3.method_10084());
		return !lv.method_11620().method_15799() || lv.method_11614() instanceof class_2349;
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_11106;
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
