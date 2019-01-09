package net.minecraft;

public class class_2272 extends class_2248 {
	public static final class_2758 field_10739 = class_2741.field_12505;
	protected static final class_265[] field_10738 = new class_265[]{
		class_2248.method_9541(1.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		class_2248.method_9541(3.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		class_2248.method_9541(5.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		class_2248.method_9541(7.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		class_2248.method_9541(9.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		class_2248.method_9541(11.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		class_2248.method_9541(13.0, 0.0, 1.0, 15.0, 8.0, 15.0)
	};

	protected class_2272(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10739, Integer.valueOf(0)));
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_10738[arg.method_11654(field_10739)];
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_2350 arg6, float f, float g, float h) {
		if (!arg2.field_9236) {
			return this.method_9719(arg2, arg3, arg, arg4);
		} else {
			class_1799 lv = arg4.method_5998(arg5);
			return this.method_9719(arg2, arg3, arg, arg4) || lv.method_7960();
		}
	}

	private boolean method_9719(class_1936 arg, class_2338 arg2, class_2680 arg3, class_1657 arg4) {
		if (!arg4.method_7332(false)) {
			return false;
		} else {
			arg4.method_7281(class_3468.field_15369);
			arg4.method_7344().method_7585(2, 0.1F);
			int i = (Integer)arg3.method_11654(field_10739);
			if (i < 6) {
				arg.method_8652(arg2, arg3.method_11657(field_10739, Integer.valueOf(i + 1)), 3);
			} else {
				arg.method_8650(arg2);
			}

			return true;
		}
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return arg2 == class_2350.field_11033 && !arg.method_11591(arg4, arg5)
			? class_2246.field_10124.method_9564()
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		return arg2.method_8320(arg3.method_10074()).method_11620().method_15799();
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10739);
	}

	@Override
	public int method_9572(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return (7 - (Integer)arg.method_11654(field_10739)) * 2;
	}

	@Override
	public boolean method_9498(class_2680 arg) {
		return true;
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
