package net.minecraft;

public class class_2261 extends class_2248 {
	protected class_2261(class_2248.class_2251 arg) {
		super(arg);
	}

	protected boolean method_9695(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		class_2248 lv = arg.method_11614();
		return lv == class_2246.field_10219
			|| lv == class_2246.field_10566
			|| lv == class_2246.field_10253
			|| lv == class_2246.field_10520
			|| lv == class_2246.field_10362;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return !arg.method_11591(arg4, arg5) ? class_2246.field_10124.method_9564() : super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2338 lv = arg3.method_10074();
		return this.method_9695(arg2.method_8320(lv), arg2, lv);
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	public boolean method_9579(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return true;
	}
}
