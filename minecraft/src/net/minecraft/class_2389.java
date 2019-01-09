package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2389 extends class_2310 {
	protected class_2389(class_2248.class_2251 arg) {
		super(1.0F, 1.0F, 16.0F, 16.0F, 16.0F, arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_10905, Boolean.valueOf(false))
				.method_11657(field_10907, Boolean.valueOf(false))
				.method_11657(field_10904, Boolean.valueOf(false))
				.method_11657(field_10903, Boolean.valueOf(false))
				.method_11657(field_10900, Boolean.valueOf(false))
		);
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_1922 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		class_3610 lv3 = arg.method_8045().method_8316(arg.method_8037());
		class_2338 lv4 = lv2.method_10095();
		class_2338 lv5 = lv2.method_10072();
		class_2338 lv6 = lv2.method_10067();
		class_2338 lv7 = lv2.method_10078();
		class_2680 lv8 = lv.method_8320(lv4);
		class_2680 lv9 = lv.method_8320(lv5);
		class_2680 lv10 = lv.method_8320(lv6);
		class_2680 lv11 = lv.method_8320(lv7);
		return this.method_9564()
			.method_11657(field_10905, Boolean.valueOf(this.method_10281(lv8, class_2248.method_9501(lv8.method_11628(lv, lv4), class_2350.field_11035))))
			.method_11657(field_10904, Boolean.valueOf(this.method_10281(lv9, class_2248.method_9501(lv9.method_11628(lv, lv5), class_2350.field_11043))))
			.method_11657(field_10903, Boolean.valueOf(this.method_10281(lv10, class_2248.method_9501(lv10.method_11628(lv, lv6), class_2350.field_11034))))
			.method_11657(field_10907, Boolean.valueOf(this.method_10281(lv11, class_2248.method_9501(lv11.method_11628(lv, lv7), class_2350.field_11039))))
			.method_11657(field_10900, Boolean.valueOf(lv3.method_15772() == class_3612.field_15910));
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if ((Boolean)arg.method_11654(field_10900)) {
			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
		}

		return arg2.method_10166().method_10179()
			? arg.method_11657(
				(class_2769)field_10902.get(arg2), Boolean.valueOf(this.method_10281(arg3, class_2248.method_9501(arg3.method_11628(arg4, arg6), arg2.method_10153())))
			)
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_9522(class_2680 arg, class_2680 arg2, class_2350 arg3) {
		if (arg2.method_11614() == this) {
			if (!arg3.method_10166().method_10179()) {
				return true;
			}

			if ((Boolean)arg.method_11654((class_2769)field_10902.get(arg3)) && (Boolean)arg2.method_11654((class_2769)field_10902.get(arg3.method_10153()))) {
				return true;
			}
		}

		return super.method_9522(arg, arg2, arg3);
	}

	public final boolean method_10281(class_2680 arg, boolean bl) {
		class_2248 lv = arg.method_11614();
		return !method_10282(lv) && bl || lv instanceof class_2389;
	}

	public static boolean method_10282(class_2248 arg) {
		return arg instanceof class_2480
			|| arg instanceof class_2397
			|| arg == class_2246.field_10327
			|| arg == class_2246.field_10593
			|| arg == class_2246.field_10171
			|| arg == class_2246.field_10295
			|| arg == class_2246.field_10174
			|| arg == class_2246.field_10560
			|| arg == class_2246.field_10615
			|| arg == class_2246.field_10379
			|| arg == class_2246.field_10545
			|| arg == class_2246.field_10261
			|| arg == class_2246.field_10147
			|| arg == class_2246.field_10009
			|| arg == class_2246.field_10499;
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9175;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10905, field_10907, field_10903, field_10904, field_10900);
	}
}
