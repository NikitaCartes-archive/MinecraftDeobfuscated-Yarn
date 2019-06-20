package net.minecraft;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class class_2286 extends class_2312 implements class_2343 {
	public static final class_2754<class_2747> field_10789 = class_2741.field_12534;

	public class_2286(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11177, class_2350.field_11043)
				.method_11657(field_10911, Boolean.valueOf(false))
				.method_11657(field_10789, class_2747.field_12576)
		);
	}

	@Override
	protected int method_9992(class_2680 arg) {
		return 2;
	}

	@Override
	protected int method_9993(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		class_2586 lv = arg.method_8321(arg2);
		return lv instanceof class_2599 ? ((class_2599)lv).method_11071() : 0;
	}

	private int method_9773(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		return arg3.method_11654(field_10789) == class_2747.field_12578
			? Math.max(this.method_9991(arg, arg2, arg3) - this.method_10000(arg, arg2, arg3), 0)
			: this.method_9991(arg, arg2, arg3);
	}

	@Override
	protected boolean method_9990(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		int i = this.method_9991(arg, arg2, arg3);
		if (i >= 15) {
			return true;
		} else {
			return i == 0 ? false : i >= this.method_10000(arg, arg2, arg3);
		}
	}

	@Override
	protected int method_9991(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		int i = super.method_9991(arg, arg2, arg3);
		class_2350 lv = arg3.method_11654(field_11177);
		class_2338 lv2 = arg2.method_10093(lv);
		class_2680 lv3 = arg.method_8320(lv2);
		if (lv3.method_11584()) {
			i = lv3.method_11627(arg, lv2);
		} else if (i < 15 && lv3.method_11621(arg, lv2)) {
			lv2 = lv2.method_10093(lv);
			lv3 = arg.method_8320(lv2);
			if (lv3.method_11584()) {
				i = lv3.method_11627(arg, lv2);
			} else if (lv3.method_11588()) {
				class_1533 lv4 = this.method_9774(arg, lv, lv2);
				if (lv4 != null) {
					i = lv4.method_6938();
				}
			}
		}

		return i;
	}

	@Nullable
	private class_1533 method_9774(class_1937 arg, class_2350 arg2, class_2338 arg3) {
		List<class_1533> list = arg.method_8390(
			class_1533.class,
			new class_238(
				(double)arg3.method_10263(),
				(double)arg3.method_10264(),
				(double)arg3.method_10260(),
				(double)(arg3.method_10263() + 1),
				(double)(arg3.method_10264() + 1),
				(double)(arg3.method_10260() + 1)
			),
			arg2x -> arg2x != null && arg2x.method_5735() == arg2
		);
		return list.size() == 1 ? (class_1533)list.get(0) : null;
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		if (!arg4.field_7503.field_7476) {
			return false;
		} else {
			arg = arg.method_11572(field_10789);
			float f = arg.method_11654(field_10789) == class_2747.field_12578 ? 0.55F : 0.5F;
			arg2.method_8396(arg4, arg3, class_3417.field_14762, class_3419.field_15245, 0.3F, f);
			arg2.method_8652(arg3, arg, 2);
			this.method_9775(arg2, arg3, arg);
			return true;
		}
	}

	@Override
	protected void method_9998(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		if (!arg.method_8397().method_8677(arg2, this)) {
			int i = this.method_9773(arg, arg2, arg3);
			class_2586 lv = arg.method_8321(arg2);
			int j = lv instanceof class_2599 ? ((class_2599)lv).method_11071() : 0;
			if (i != j || (Boolean)arg3.method_11654(field_10911) != this.method_9990(arg, arg2, arg3)) {
				class_1953 lv2 = this.method_9988(arg, arg2, arg3) ? class_1953.field_9310 : class_1953.field_9314;
				arg.method_8397().method_8675(arg2, this, 2, lv2);
			}
		}
	}

	private void method_9775(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		int i = this.method_9773(arg, arg2, arg3);
		class_2586 lv = arg.method_8321(arg2);
		int j = 0;
		if (lv instanceof class_2599) {
			class_2599 lv2 = (class_2599)lv;
			j = lv2.method_11071();
			lv2.method_11070(i);
		}

		if (j != i || arg3.method_11654(field_10789) == class_2747.field_12576) {
			boolean bl = this.method_9990(arg, arg2, arg3);
			boolean bl2 = (Boolean)arg3.method_11654(field_10911);
			if (bl2 && !bl) {
				arg.method_8652(arg2, arg3.method_11657(field_10911, Boolean.valueOf(false)), 2);
			} else if (!bl2 && bl) {
				arg.method_8652(arg2, arg3.method_11657(field_10911, Boolean.valueOf(true)), 2);
			}

			this.method_9997(arg, arg2, arg3);
		}
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		this.method_9775(arg2, arg3, arg);
	}

	@Override
	public boolean method_9592(class_2680 arg, class_1937 arg2, class_2338 arg3, int i, int j) {
		super.method_9592(arg, arg2, arg3, i, j);
		class_2586 lv = arg2.method_8321(arg3);
		return lv != null && lv.method_11004(i, j);
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2599();
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11177, field_10789, field_10911);
	}
}
