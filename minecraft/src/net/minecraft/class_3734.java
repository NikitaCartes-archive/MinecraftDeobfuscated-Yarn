package net.minecraft;

import javax.annotation.Nullable;

public class class_3734 extends class_1747 {
	public class_3734(class_2248 arg, class_1792.class_1793 arg2) {
		super(arg, arg2);
	}

	@Nullable
	@Override
	public class_1750 method_16356(class_1750 arg) {
		class_2338 lv = arg.method_8037();
		class_1937 lv2 = arg.method_8045();
		class_2680 lv3 = lv2.method_8320(lv);
		class_2248 lv4 = this.method_7711();
		if (lv3.method_11614() != lv4) {
			return arg;
		} else {
			class_2350 lv5;
			if (arg.method_8046()) {
				lv5 = arg.method_17699() ? arg.method_8038().method_10153() : arg.method_8038();
			} else {
				lv5 = arg.method_8038() == class_2350.field_11036 ? arg.method_8042() : class_2350.field_11036;
			}

			int i = 0;
			class_2338.class_2339 lv6 = new class_2338.class_2339(lv).method_10098(lv5);

			while (i < 7) {
				if (!lv2.field_9236 && !class_1937.method_8558(lv6)) {
					class_1657 lv7 = arg.method_8036();
					int j = lv2.method_8322();
					if (lv7 instanceof class_3222 && lv6.method_10264() >= j) {
						class_2635 lv8 = new class_2635(new class_2588("build.tooHigh", j).method_10854(class_124.field_1061), class_2556.field_11733);
						((class_3222)lv7).field_13987.method_14364(lv8);
					}
					break;
				}

				lv3 = lv2.method_8320(lv6);
				if (lv3.method_11614() != this.method_7711()) {
					if (lv3.method_11587(arg)) {
						return class_1750.method_16355(arg, lv6, lv5);
					}
					break;
				}

				lv6.method_10098(lv5);
				if (lv5.method_10166().method_10179()) {
					i++;
				}
			}

			return null;
		}
	}
}
