package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2401 extends class_2341 {
	public static final class_2746 field_11265 = class_2741.field_12484;
	protected static final class_265 field_11267 = class_2248.method_9541(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
	protected static final class_265 field_11263 = class_2248.method_9541(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
	protected static final class_265 field_11260 = class_2248.method_9541(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
	protected static final class_265 field_11262 = class_2248.method_9541(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
	protected static final class_265 field_11264 = class_2248.method_9541(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
	protected static final class_265 field_11261 = class_2248.method_9541(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
	protected static final class_265 field_11268 = class_2248.method_9541(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
	protected static final class_265 field_11266 = class_2248.method_9541(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);

	protected class_2401(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11177, class_2350.field_11043)
				.method_11657(field_11265, Boolean.valueOf(false))
				.method_11657(field_11007, class_2738.field_12471)
		);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		switch ((class_2738)arg.method_11654(field_11007)) {
			case field_12475:
				switch (((class_2350)arg.method_11654(field_11177)).method_10166()) {
					case field_11048:
						return field_11261;
					case field_11051:
					default:
						return field_11264;
				}
			case field_12471:
				switch ((class_2350)arg.method_11654(field_11177)) {
					case field_11034:
						return field_11262;
					case field_11039:
						return field_11260;
					case field_11035:
						return field_11263;
					case field_11043:
					default:
						return field_11267;
				}
			case field_12473:
			default:
				switch (((class_2350)arg.method_11654(field_11177)).method_10166()) {
					case field_11048:
						return field_11266;
					case field_11051:
					default:
						return field_11268;
				}
		}
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		arg = arg.method_11572(field_11265);
		boolean bl = (Boolean)arg.method_11654(field_11265);
		if (arg2.field_9236) {
			if (bl) {
				method_10308(arg, arg2, arg3, 1.0F);
			}

			return true;
		} else {
			arg2.method_8652(arg3, arg, 3);
			float f = bl ? 0.6F : 0.5F;
			arg2.method_8396(null, arg3, class_3417.field_14962, class_3419.field_15245, 0.3F, f);
			this.method_10309(arg, arg2, arg3);
			return true;
		}
	}

	private static void method_10308(class_2680 arg, class_1936 arg2, class_2338 arg3, float f) {
		class_2350 lv = ((class_2350)arg.method_11654(field_11177)).method_10153();
		class_2350 lv2 = method_10119(arg).method_10153();
		double d = (double)arg3.method_10263() + 0.5 + 0.1 * (double)lv.method_10148() + 0.2 * (double)lv2.method_10148();
		double e = (double)arg3.method_10264() + 0.5 + 0.1 * (double)lv.method_10164() + 0.2 * (double)lv2.method_10164();
		double g = (double)arg3.method_10260() + 0.5 + 0.1 * (double)lv.method_10165() + 0.2 * (double)lv2.method_10165();
		arg2.method_8406(new class_2390(1.0F, 0.0F, 0.0F, f), d, e, g, 0.0, 0.0, 0.0);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if ((Boolean)arg.method_11654(field_11265) && random.nextFloat() < 0.25F) {
			method_10308(arg, arg2, arg3, 0.5F);
		}
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (!bl && arg.method_11614() != arg4.method_11614()) {
			if ((Boolean)arg.method_11654(field_11265)) {
				this.method_10309(arg, arg2, arg3);
			}

			super.method_9536(arg, arg2, arg3, arg4, bl);
		}
	}

	@Override
	public int method_9524(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return arg.method_11654(field_11265) ? 15 : 0;
	}

	@Override
	public int method_9603(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return arg.method_11654(field_11265) && method_10119(arg) == arg4 ? 15 : 0;
	}

	@Override
	public boolean method_9506(class_2680 arg) {
		return true;
	}

	private void method_10309(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		arg2.method_8452(arg3, this);
		arg2.method_8452(arg3.method_10093(method_10119(arg).method_10153()), this);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11007, field_11177, field_11265);
	}
}
