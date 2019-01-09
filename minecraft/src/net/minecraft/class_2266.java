package net.minecraft;

import java.util.Random;

public class class_2266 extends class_2248 {
	public static final class_2758 field_10709 = class_2741.field_12498;
	protected static final class_265 field_10711 = class_2248.method_9541(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
	protected static final class_265 field_10710 = class_2248.method_9541(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	protected class_2266(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10709, Integer.valueOf(0)));
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!arg.method_11591(arg2, arg3)) {
			arg2.method_8651(arg3, true);
		} else {
			class_2338 lv = arg3.method_10084();
			if (arg2.method_8623(lv)) {
				int i = 1;

				while (arg2.method_8320(arg3.method_10087(i)).method_11614() == this) {
					i++;
				}

				if (i < 3) {
					int j = (Integer)arg.method_11654(field_10709);
					if (j == 15) {
						arg2.method_8501(lv, this.method_9564());
						class_2680 lv2 = arg.method_11657(field_10709, Integer.valueOf(0));
						arg2.method_8652(arg3, lv2, 4);
						lv2.method_11622(arg2, lv, this, arg3);
					} else {
						arg2.method_8652(arg3, arg.method_11657(field_10709, Integer.valueOf(j + 1)), 4);
					}
				}
			}
		}
	}

	@Override
	public class_265 method_9549(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_10711;
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_10710;
	}

	@Override
	public boolean method_9601(class_2680 arg) {
		return true;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (!arg.method_11591(arg4, arg5)) {
			arg4.method_8397().method_8676(arg5, this, 1);
		}

		return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		for (class_2350 lv : class_2350.class_2353.field_11062) {
			class_2680 lv2 = arg2.method_8320(arg3.method_10093(lv));
			class_3614 lv3 = lv2.method_11620();
			if (lv3.method_15799() || arg2.method_8316(arg3.method_10093(lv)).method_15767(class_3486.field_15518)) {
				return false;
			}
		}

		class_2248 lv4 = arg2.method_8320(arg3.method_10074()).method_11614();
		return (lv4 == class_2246.field_10029 || lv4 == class_2246.field_10102 || lv4 == class_2246.field_10534)
			&& !arg2.method_8320(arg3.method_10084()).method_11620().method_15797();
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		arg4.method_5643(class_1282.field_5848, 1.0F);
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10709);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
