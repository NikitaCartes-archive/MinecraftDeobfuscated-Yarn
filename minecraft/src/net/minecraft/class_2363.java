package net.minecraft;

public abstract class class_2363 extends class_2237 {
	public static final class_2753 field_11104 = class_2383.field_11177;
	public static final class_2746 field_11105 = class_2459.field_11446;

	protected class_2363(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11104, class_2350.field_11043).method_11657(field_11105, Boolean.valueOf(false)));
	}

	@Override
	public int method_9593(class_2680 arg) {
		return arg.method_11654(field_11105) ? super.method_9593(arg) : 0;
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		if (!arg2.field_9236) {
			this.method_17025(arg2, arg3, arg4);
		}

		return true;
	}

	protected abstract void method_17025(class_1937 arg, class_2338 arg2, class_1657 arg3);

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_11104, arg.method_8042().method_10153());
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1309 arg4, class_1799 arg5) {
		if (arg5.method_7938()) {
			class_2586 lv = arg.method_8321(arg2);
			if (lv instanceof class_2609) {
				((class_2609)lv).method_17488(arg5.method_7964());
			}
		}
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg4.method_11614()) {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_2609) {
				class_1264.method_5451(arg2, arg3, (class_2609)lv);
				arg2.method_8455(arg3, this);
			}

			super.method_9536(arg, arg2, arg3, arg4, bl);
		}
	}

	@Override
	public boolean method_9498(class_2680 arg) {
		return true;
	}

	@Override
	public int method_9572(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return class_1703.method_7608(arg2.method_8321(arg3));
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11458;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_11104, arg2.method_10503(arg.method_11654(field_11104)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_11104)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11104, field_11105);
	}
}
