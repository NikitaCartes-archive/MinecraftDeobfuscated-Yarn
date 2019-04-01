package net.minecraft;

public class class_2387 extends class_2237 {
	public static final class_2746 field_11180 = class_2741.field_12544;

	protected class_2387(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11180, Boolean.valueOf(false)));
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		if ((Boolean)arg.method_11654(field_11180)) {
			this.method_10277(arg2, arg3);
			arg = arg.method_11657(field_11180, Boolean.valueOf(false));
			arg2.method_8652(arg3, arg, 2);
			return true;
		} else {
			return false;
		}
	}

	public void method_10276(class_1936 arg, class_2338 arg2, class_2680 arg3, class_1799 arg4) {
		class_2586 lv = arg.method_8321(arg2);
		if (lv instanceof class_2619) {
			((class_2619)lv).method_11276(arg4.method_7972());
			arg.method_8652(arg2, arg3.method_11657(field_11180, Boolean.valueOf(true)), 2);
		}
	}

	private void method_10277(class_1937 arg, class_2338 arg2) {
		if (!arg.field_9236) {
			class_2586 lv = arg.method_8321(arg2);
			if (lv instanceof class_2619) {
				class_2619 lv2 = (class_2619)lv;
				class_1799 lv3 = lv2.method_11275();
				if (!lv3.method_7960()) {
					arg.method_8535(1010, arg2, 0);
					lv2.method_5448();
					float f = 0.7F;
					double d = (double)(arg.field_9229.nextFloat() * 0.7F) + 0.15F;
					double e = (double)(arg.field_9229.nextFloat() * 0.7F) + 0.060000002F + 0.6;
					double g = (double)(arg.field_9229.nextFloat() * 0.7F) + 0.15F;
					class_1799 lv4 = lv3.method_7972();
					class_1542 lv5 = new class_1542(arg, (double)arg2.method_10263() + d, (double)arg2.method_10264() + e, (double)arg2.method_10260() + g, lv4);
					lv5.method_6988();
					arg.method_8649(lv5);
				}
			}
		}
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg4.method_11614()) {
			this.method_10277(arg2, arg3);
			super.method_9536(arg, arg2, arg3, arg4, bl);
		}
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2619();
	}

	@Override
	public boolean method_9498(class_2680 arg) {
		return true;
	}

	@Override
	public int method_9572(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		class_2586 lv = arg2.method_8321(arg3);
		if (lv instanceof class_2619) {
			class_1792 lv2 = ((class_2619)lv).method_11275().method_7909();
			if (lv2 instanceof class_1813) {
				return ((class_1813)lv2).method_8010();
			}
		}

		return 0;
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11458;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11180);
	}
}
