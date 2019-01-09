package net.minecraft;

public class class_2354 extends class_2310 {
	private final class_265[] field_11066;

	public class_2354(class_2248.class_2251 arg) {
		super(2.0F, 2.0F, 16.0F, 16.0F, 24.0F, arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_10905, Boolean.valueOf(false))
				.method_11657(field_10907, Boolean.valueOf(false))
				.method_11657(field_10904, Boolean.valueOf(false))
				.method_11657(field_10903, Boolean.valueOf(false))
				.method_11657(field_10900, Boolean.valueOf(false))
		);
		this.field_11066 = this.method_9984(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
	}

	@Override
	public class_265 method_9571(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return this.field_11066[this.method_9987(arg)];
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}

	public boolean method_10184(class_2680 arg, boolean bl, class_2350 arg2) {
		class_2248 lv = arg.method_11614();
		boolean bl2 = lv.method_9525(class_3481.field_16584) && arg.method_11620() == this.field_10635;
		boolean bl3 = lv instanceof class_2349 && class_2349.method_16703(arg, arg2);
		return !method_10185(lv) && bl || bl2 || bl3;
	}

	public static boolean method_10185(class_2248 arg) {
		return class_2248.method_9581(arg)
			|| arg == class_2246.field_10499
			|| arg == class_2246.field_10545
			|| arg == class_2246.field_10261
			|| arg == class_2246.field_10147
			|| arg == class_2246.field_10009
			|| arg == class_2246.field_10110
			|| arg == class_2246.field_10375;
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_2350 arg6, float f, float g, float h) {
		if (!arg2.field_9236) {
			return class_1804.method_7994(arg4, arg2, arg3);
		} else {
			class_1799 lv = arg4.method_5998(arg5);
			return lv.method_7909() == class_1802.field_8719 || lv.method_7960();
		}
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_1922 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		class_3610 lv3 = arg.method_8045().method_8316(arg.method_8037());
		class_2338 lv4 = lv2.method_10095();
		class_2338 lv5 = lv2.method_10078();
		class_2338 lv6 = lv2.method_10072();
		class_2338 lv7 = lv2.method_10067();
		class_2680 lv8 = lv.method_8320(lv4);
		class_2680 lv9 = lv.method_8320(lv5);
		class_2680 lv10 = lv.method_8320(lv6);
		class_2680 lv11 = lv.method_8320(lv7);
		return super.method_9605(arg)
			.method_11657(
				field_10905, Boolean.valueOf(this.method_10184(lv8, class_2248.method_9501(lv8.method_11628(lv, lv4), class_2350.field_11035), class_2350.field_11035))
			)
			.method_11657(
				field_10907, Boolean.valueOf(this.method_10184(lv9, class_2248.method_9501(lv9.method_11628(lv, lv5), class_2350.field_11039), class_2350.field_11039))
			)
			.method_11657(
				field_10904, Boolean.valueOf(this.method_10184(lv10, class_2248.method_9501(lv10.method_11628(lv, lv6), class_2350.field_11043), class_2350.field_11043))
			)
			.method_11657(
				field_10903, Boolean.valueOf(this.method_10184(lv11, class_2248.method_9501(lv11.method_11628(lv, lv7), class_2350.field_11034), class_2350.field_11034))
			)
			.method_11657(field_10900, Boolean.valueOf(lv3.method_15772() == class_3612.field_15910));
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if ((Boolean)arg.method_11654(field_10900)) {
			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
		}

		return arg2.method_10166().method_10180() == class_2350.class_2353.field_11062
			? arg.method_11657(
				(class_2769)field_10902.get(arg2),
				Boolean.valueOf(this.method_10184(arg3, class_2248.method_9501(arg3.method_11628(arg4, arg6), arg2.method_10153()), arg2.method_10153()))
			)
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10905, field_10907, field_10903, field_10904, field_10900);
	}
}
