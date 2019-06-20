package net.minecraft;

public class class_2238 extends class_2237 implements class_4275 {
	public class_2238(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_1767 method_10622() {
		return class_1767.field_7952;
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2580();
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		if (arg2.field_9236) {
			return true;
		} else {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_2580) {
				arg4.method_17355((class_2580)lv);
				arg4.method_7281(class_3468.field_15416);
			}

			return true;
		}
	}

	@Override
	public boolean method_9521(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return false;
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11458;
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1309 arg4, class_1799 arg5) {
		if (arg5.method_7938()) {
			class_2586 lv = arg.method_8321(arg2);
			if (lv instanceof class_2580) {
				((class_2580)lv).method_10936(arg5.method_7964());
			}
		}
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}
}
