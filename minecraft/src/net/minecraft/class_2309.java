package net.minecraft;

public class class_2309 extends class_2237 {
	public static final class_2758 field_10897 = class_2741.field_12511;
	public static final class_2746 field_10899 = class_2741.field_12501;
	protected static final class_265 field_10898 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);

	public class_2309(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10897, Integer.valueOf(0)).method_11657(field_10899, Boolean.valueOf(false)));
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_10898;
	}

	@Override
	public boolean method_9526(class_2680 arg) {
		return true;
	}

	@Override
	public int method_9524(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return (Integer)arg.method_11654(field_10897);
	}

	public static void method_9983(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		if (arg2.field_9247.method_12451()) {
			int i = arg2.method_8314(class_1944.field_9284, arg3) - arg2.method_8594();
			float f = arg2.method_8442(1.0F);
			boolean bl = (Boolean)arg.method_11654(field_10899);
			if (bl) {
				i = 15 - i;
			} else if (i > 0) {
				float g = f < (float) Math.PI ? 0.0F : (float) (Math.PI * 2);
				f += (g - f) * 0.2F;
				i = Math.round((float)i * class_3532.method_15362(f));
			}

			i = class_3532.method_15340(i, 0, 15);
			if ((Integer)arg.method_11654(field_10897) != i) {
				arg2.method_8652(arg3, arg.method_11657(field_10897, Integer.valueOf(i)), 3);
			}
		}
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		if (arg4.method_7294()) {
			if (arg2.field_9236) {
				return true;
			} else {
				class_2680 lv = arg.method_11572(field_10899);
				arg2.method_8652(arg3, lv, 4);
				method_9983(lv, arg2, arg3);
				return true;
			}
		} else {
			return super.method_9534(arg, arg2, arg3, arg4, arg5, arg6);
		}
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11458;
	}

	@Override
	public boolean method_9506(class_2680 arg) {
		return true;
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2603();
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10897, field_10899);
	}
}
