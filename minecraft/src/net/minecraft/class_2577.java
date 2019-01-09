package net.minecraft;

public class class_2577 extends class_2248 {
	protected static final class_265 field_11783 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
	private final class_1767 field_11784;

	protected class_2577(class_1767 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_11784 = arg;
	}

	public class_1767 method_10925() {
		return this.field_11784;
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_11783;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return !arg.method_11591(arg4, arg5) ? class_2246.field_10124.method_9564() : super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		return !arg2.method_8623(arg3.method_10074());
	}
}
