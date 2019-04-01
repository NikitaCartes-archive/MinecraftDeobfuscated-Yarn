package net.minecraft;

public class class_2484 extends class_2190 {
	public static final class_2758 field_11505 = class_2741.field_12532;
	protected static final class_265 field_11506 = class_2248.method_9541(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);

	protected class_2484(class_2484.class_2485 arg, class_2248.class_2251 arg2) {
		super(arg, arg2);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11505, Integer.valueOf(0)));
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_11506;
	}

	@Override
	public class_265 method_9571(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return class_259.method_1073();
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_11505, Integer.valueOf(class_3532.method_15357((double)(arg.method_8044() * 16.0F / 360.0F) + 0.5) & 15));
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_11505, Integer.valueOf(arg2.method_10502((Integer)arg.method_11654(field_11505), 16)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11657(field_11505, Integer.valueOf(arg2.method_10344((Integer)arg.method_11654(field_11505), 16)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11505);
	}

	public interface class_2485 {
	}

	public static enum class_2486 implements class_2484.class_2485 {
		field_11512,
		field_11513,
		field_11510,
		field_11508,
		field_11507,
		field_11511;
	}
}
