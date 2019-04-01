package net.minecraft;

import java.util.Map;

public class class_2381 extends class_2248 {
	public static final class_2746 field_11171 = class_2429.field_11332;
	public static final class_2746 field_11172 = class_2429.field_11335;
	public static final class_2746 field_11170 = class_2429.field_11331;
	public static final class_2746 field_11167 = class_2429.field_11328;
	public static final class_2746 field_11166 = class_2429.field_11327;
	public static final class_2746 field_11169 = class_2429.field_11330;
	private static final Map<class_2350, class_2746> field_11168 = class_2429.field_11329;

	public class_2381(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11171, Boolean.valueOf(true))
				.method_11657(field_11172, Boolean.valueOf(true))
				.method_11657(field_11170, Boolean.valueOf(true))
				.method_11657(field_11167, Boolean.valueOf(true))
				.method_11657(field_11166, Boolean.valueOf(true))
				.method_11657(field_11169, Boolean.valueOf(true))
		);
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_1922 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		return this.method_9564()
			.method_11657(field_11169, Boolean.valueOf(this != lv.method_8320(lv2.method_10074()).method_11614()))
			.method_11657(field_11166, Boolean.valueOf(this != lv.method_8320(lv2.method_10084()).method_11614()))
			.method_11657(field_11171, Boolean.valueOf(this != lv.method_8320(lv2.method_10095()).method_11614()))
			.method_11657(field_11172, Boolean.valueOf(this != lv.method_8320(lv2.method_10078()).method_11614()))
			.method_11657(field_11170, Boolean.valueOf(this != lv.method_8320(lv2.method_10072()).method_11614()))
			.method_11657(field_11167, Boolean.valueOf(this != lv.method_8320(lv2.method_10067()).method_11614()));
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return arg3.method_11614() == this
			? arg.method_11657((class_2769)field_11168.get(arg2), Boolean.valueOf(false))
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657((class_2769)field_11168.get(arg2.method_10503(class_2350.field_11043)), arg.method_11654(field_11171))
			.method_11657((class_2769)field_11168.get(arg2.method_10503(class_2350.field_11035)), arg.method_11654(field_11170))
			.method_11657((class_2769)field_11168.get(arg2.method_10503(class_2350.field_11034)), arg.method_11654(field_11172))
			.method_11657((class_2769)field_11168.get(arg2.method_10503(class_2350.field_11039)), arg.method_11654(field_11167))
			.method_11657((class_2769)field_11168.get(arg2.method_10503(class_2350.field_11036)), arg.method_11654(field_11166))
			.method_11657((class_2769)field_11168.get(arg2.method_10503(class_2350.field_11033)), arg.method_11654(field_11169));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11657((class_2769)field_11168.get(arg2.method_10343(class_2350.field_11043)), arg.method_11654(field_11171))
			.method_11657((class_2769)field_11168.get(arg2.method_10343(class_2350.field_11035)), arg.method_11654(field_11170))
			.method_11657((class_2769)field_11168.get(arg2.method_10343(class_2350.field_11034)), arg.method_11654(field_11172))
			.method_11657((class_2769)field_11168.get(arg2.method_10343(class_2350.field_11039)), arg.method_11654(field_11167))
			.method_11657((class_2769)field_11168.get(arg2.method_10343(class_2350.field_11036)), arg.method_11654(field_11166))
			.method_11657((class_2769)field_11168.get(arg2.method_10343(class_2350.field_11033)), arg.method_11654(field_11169));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11166, field_11169, field_11171, field_11172, field_11170, field_11167);
	}
}
