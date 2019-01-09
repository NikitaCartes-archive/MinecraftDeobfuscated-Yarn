package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2513 extends class_2261 implements class_2256 {
	public static final class_2758 field_11584 = class_2741.field_12550;
	protected static final class_265[] field_11583 = new class_265[]{
		class_2248.method_9541(7.0, 0.0, 7.0, 9.0, 2.0, 9.0),
		class_2248.method_9541(7.0, 0.0, 7.0, 9.0, 4.0, 9.0),
		class_2248.method_9541(7.0, 0.0, 7.0, 9.0, 6.0, 9.0),
		class_2248.method_9541(7.0, 0.0, 7.0, 9.0, 8.0, 9.0),
		class_2248.method_9541(7.0, 0.0, 7.0, 9.0, 10.0, 9.0),
		class_2248.method_9541(7.0, 0.0, 7.0, 9.0, 12.0, 9.0),
		class_2248.method_9541(7.0, 0.0, 7.0, 9.0, 14.0, 9.0),
		class_2248.method_9541(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)
	};
	private final class_2511 field_11585;

	protected class_2513(class_2511 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_11585 = arg;
		this.method_9590(this.field_10647.method_11664().method_11657(field_11584, Integer.valueOf(0)));
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_11583[arg.method_11654(field_11584)];
	}

	@Override
	protected boolean method_9695(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return arg.method_11614() == class_2246.field_10362;
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		super.method_9588(arg, arg2, arg3, random);
		if (arg2.method_8624(arg3.method_10084(), 0) >= 9) {
			float f = class_2302.method_9830(this, arg2, arg3);
			if (random.nextInt((int)(25.0F / f) + 1) == 0) {
				int i = (Integer)arg.method_11654(field_11584);
				if (i < 7) {
					arg = arg.method_11657(field_11584, Integer.valueOf(i + 1));
					arg2.method_8652(arg3, arg, 2);
				} else {
					class_2350 lv = class_2350.class_2353.field_11062.method_10183(random);
					class_2338 lv2 = arg3.method_10093(lv);
					class_2248 lv3 = arg2.method_8320(lv2.method_10074()).method_11614();
					if (arg2.method_8320(lv2).method_11588()
						&& (
							lv3 == class_2246.field_10362
								|| lv3 == class_2246.field_10566
								|| lv3 == class_2246.field_10253
								|| lv3 == class_2246.field_10520
								|| lv3 == class_2246.field_10219
						)) {
						arg2.method_8501(lv2, this.field_11585.method_9564());
						arg2.method_8501(arg3, this.field_11585.method_10680().method_9564().method_11657(class_2383.field_11177, lv));
					}
				}
			}
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	protected class_1792 method_10695() {
		if (this.field_11585 == class_2246.field_10261) {
			return class_1802.field_8706;
		} else {
			return this.field_11585 == class_2246.field_10545 ? class_1802.field_8188 : null;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		class_1792 lv = this.method_10695();
		return lv == null ? class_1799.field_8037 : new class_1799(lv);
	}

	@Override
	public boolean method_9651(class_1922 arg, class_2338 arg2, class_2680 arg3, boolean bl) {
		return (Integer)arg3.method_11654(field_11584) != 7;
	}

	@Override
	public boolean method_9650(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		return true;
	}

	@Override
	public void method_9652(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		int i = Math.min(7, (Integer)arg3.method_11654(field_11584) + class_3532.method_15395(arg.field_9229, 2, 5));
		class_2680 lv = arg3.method_11657(field_11584, Integer.valueOf(i));
		arg.method_8652(arg2, lv, 2);
		if (i == 7) {
			lv.method_11585(arg, arg2, arg.field_9229);
		}
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11584);
	}

	public class_2511 method_10694() {
		return this.field_11585;
	}
}
