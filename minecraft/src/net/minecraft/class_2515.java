package net.minecraft;

import javax.annotation.Nullable;

public class class_2515 extends class_2237 {
	public static final class_2754<class_2776> field_11586 = class_2741.field_12547;

	protected class_2515(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2633();
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		class_2586 lv = arg2.method_8321(arg3);
		return lv instanceof class_2633 ? ((class_2633)lv).method_11351(arg4) : false;
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, @Nullable class_1309 arg4, class_1799 arg5) {
		if (!arg.field_9236) {
			if (arg4 != null) {
				class_2586 lv = arg.method_8321(arg2);
				if (lv instanceof class_2633) {
					((class_2633)lv).method_11373(arg4);
				}
			}
		}
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11458;
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_11586, class_2776.field_12696);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11586);
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5, boolean bl) {
		if (!arg2.field_9236) {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_2633) {
				class_2633 lv2 = (class_2633)lv;
				boolean bl2 = arg2.method_8479(arg3);
				boolean bl3 = lv2.method_11354();
				if (bl2 && !bl3) {
					lv2.method_11379(true);
					this.method_10703(lv2);
				} else if (!bl2 && bl3) {
					lv2.method_11379(false);
				}
			}
		}
	}

	private void method_10703(class_2633 arg) {
		switch (arg.method_11374()) {
			case field_12695:
				arg.method_11366(false);
				break;
			case field_12697:
				arg.method_11368(false);
				break;
			case field_12699:
				arg.method_11361();
			case field_12696:
		}
	}
}
