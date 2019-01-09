package net.minecraft;

import java.util.Map;

public class class_2310 extends class_2248 implements class_3737 {
	public static final class_2746 field_10905 = class_2429.field_11332;
	public static final class_2746 field_10907 = class_2429.field_11335;
	public static final class_2746 field_10904 = class_2429.field_11331;
	public static final class_2746 field_10903 = class_2429.field_11328;
	public static final class_2746 field_10900 = class_2741.field_12508;
	protected static final Map<class_2350, class_2746> field_10902 = (Map<class_2350, class_2746>)class_2429.field_11329
		.entrySet()
		.stream()
		.filter(entry -> ((class_2350)entry.getKey()).method_10166().method_10179())
		.collect(class_156.method_664());
	protected final class_265[] field_10901;
	protected final class_265[] field_10906;

	protected class_2310(float f, float g, float h, float i, float j, class_2248.class_2251 arg) {
		super(arg);
		this.field_10901 = this.method_9984(f, g, j, 0.0F, j);
		this.field_10906 = this.method_9984(f, g, h, 0.0F, i);
	}

	protected class_265[] method_9984(float f, float g, float h, float i, float j) {
		float k = 8.0F - f;
		float l = 8.0F + f;
		float m = 8.0F - g;
		float n = 8.0F + g;
		class_265 lv = class_2248.method_9541((double)k, 0.0, (double)k, (double)l, (double)h, (double)l);
		class_265 lv2 = class_2248.method_9541((double)m, (double)i, 0.0, (double)n, (double)j, (double)n);
		class_265 lv3 = class_2248.method_9541((double)m, (double)i, (double)m, (double)n, (double)j, 16.0);
		class_265 lv4 = class_2248.method_9541(0.0, (double)i, (double)m, (double)n, (double)j, (double)n);
		class_265 lv5 = class_2248.method_9541((double)m, (double)i, (double)m, 16.0, (double)j, (double)n);
		class_265 lv6 = class_259.method_1084(lv2, lv5);
		class_265 lv7 = class_259.method_1084(lv3, lv4);
		class_265[] lvs = new class_265[]{
			class_259.method_1073(),
			lv3,
			lv4,
			lv7,
			lv2,
			class_259.method_1084(lv3, lv2),
			class_259.method_1084(lv4, lv2),
			class_259.method_1084(lv7, lv2),
			lv5,
			class_259.method_1084(lv3, lv5),
			class_259.method_1084(lv4, lv5),
			class_259.method_1084(lv7, lv5),
			lv6,
			class_259.method_1084(lv3, lv6),
			class_259.method_1084(lv4, lv6),
			class_259.method_1084(lv7, lv6)
		};

		for (int o = 0; o < 16; o++) {
			lvs[o] = class_259.method_1084(lv, lvs[o]);
		}

		return lvs;
	}

	@Override
	public boolean method_9579(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return !(Boolean)arg.method_11654(field_10900);
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return this.field_10906[this.method_9987(arg)];
	}

	@Override
	public class_265 method_9549(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return this.field_10901[this.method_9987(arg)];
	}

	private static int method_9985(class_2350 arg) {
		return 1 << arg.method_10161();
	}

	protected int method_9987(class_2680 arg) {
		int i = 0;
		if ((Boolean)arg.method_11654(field_10905)) {
			i |= method_9985(class_2350.field_11043);
		}

		if ((Boolean)arg.method_11654(field_10907)) {
			i |= method_9985(class_2350.field_11034);
		}

		if ((Boolean)arg.method_11654(field_10904)) {
			i |= method_9985(class_2350.field_11035);
		}

		if ((Boolean)arg.method_11654(field_10903)) {
			i |= method_9985(class_2350.field_11039);
		}

		return i;
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		return arg.method_11654(field_10900) ? class_3612.field_15910.method_15729(false) : super.method_9545(arg);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		switch (arg2) {
			case field_11464:
				return arg.method_11657(field_10905, arg.method_11654(field_10904))
					.method_11657(field_10907, arg.method_11654(field_10903))
					.method_11657(field_10904, arg.method_11654(field_10905))
					.method_11657(field_10903, arg.method_11654(field_10907));
			case field_11465:
				return arg.method_11657(field_10905, arg.method_11654(field_10907))
					.method_11657(field_10907, arg.method_11654(field_10904))
					.method_11657(field_10904, arg.method_11654(field_10903))
					.method_11657(field_10903, arg.method_11654(field_10905));
			case field_11463:
				return arg.method_11657(field_10905, arg.method_11654(field_10903))
					.method_11657(field_10907, arg.method_11654(field_10905))
					.method_11657(field_10904, arg.method_11654(field_10907))
					.method_11657(field_10903, arg.method_11654(field_10904));
			default:
				return arg;
		}
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		switch (arg2) {
			case field_11300:
				return arg.method_11657(field_10905, arg.method_11654(field_10904)).method_11657(field_10904, arg.method_11654(field_10905));
			case field_11301:
				return arg.method_11657(field_10907, arg.method_11654(field_10903)).method_11657(field_10903, arg.method_11654(field_10907));
			default:
				return super.method_9569(arg, arg2);
		}
	}
}
