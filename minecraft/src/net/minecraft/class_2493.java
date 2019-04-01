package net.minecraft;

public class class_2493 extends class_2248 {
	public static final class_2746 field_11522 = class_2741.field_12512;

	protected class_2493(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11522, Boolean.valueOf(false)));
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (arg2 != class_2350.field_11036) {
			return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
		} else {
			class_2248 lv = arg3.method_11614();
			return arg.method_11657(field_11522, Boolean.valueOf(lv == class_2246.field_10491 || lv == class_2246.field_10477));
		}
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2248 lv = arg.method_8045().method_8320(arg.method_8037().method_10084()).method_11614();
		return this.method_9564().method_11657(field_11522, Boolean.valueOf(lv == class_2246.field_10491 || lv == class_2246.field_10477));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11522);
	}
}
