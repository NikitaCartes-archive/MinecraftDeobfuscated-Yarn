package net.minecraft;

public class class_2508 extends class_2478 {
	public static final class_2758 field_11559 = class_2741.field_12532;

	public class_2508(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11559, Integer.valueOf(0)).method_11657(field_11491, Boolean.valueOf(false)));
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		return arg2.method_8320(arg3.method_10074()).method_11620().method_15799();
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_3610 lv = arg.method_8045().method_8316(arg.method_8037());
		return this.method_9564()
			.method_11657(field_11559, Integer.valueOf(class_3532.method_15357((double)((180.0F + arg.method_8044()) * 16.0F / 360.0F) + 0.5) & 15))
			.method_11657(field_11491, Boolean.valueOf(lv.method_15772() == class_3612.field_15910));
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return arg2 == class_2350.field_11033 && !this.method_9558(arg, arg4, arg5)
			? class_2246.field_10124.method_9564()
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_11559, Integer.valueOf(arg2.method_10502((Integer)arg.method_11654(field_11559), 16)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11657(field_11559, Integer.valueOf(arg2.method_10344((Integer)arg.method_11654(field_11559), 16)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11559, field_11491);
	}
}
