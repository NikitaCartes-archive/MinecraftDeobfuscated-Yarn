package net.minecraft;

import java.util.function.Predicate;

public class class_2276 extends class_2383 {
	public static final class_2753 field_10748 = class_2383.field_11177;
	private class_2700 field_10749;
	private class_2700 field_10750;
	private class_2700 field_10752;
	private class_2700 field_10753;
	private static final Predicate<class_2680> field_10751 = arg -> arg != null
			&& (arg.method_11614() == class_2246.field_10147 || arg.method_11614() == class_2246.field_10009);

	protected class_2276(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10748, class_2350.field_11043));
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4) {
		if (arg4.method_11614() != arg.method_11614()) {
			this.method_9731(arg2, arg3);
		}
	}

	public boolean method_9733(class_1941 arg, class_2338 arg2) {
		return this.method_9732().method_11708(arg, arg2) != null || this.method_9727().method_11708(arg, arg2) != null;
	}

	private void method_9731(class_1937 arg, class_2338 arg2) {
		class_2700.class_2702 lv = this.method_9729().method_11708(arg, arg2);
		if (lv != null) {
			for (int i = 0; i < this.method_9729().method_11713(); i++) {
				class_2694 lv2 = lv.method_11717(0, i, 0);
				arg.method_8652(lv2.method_11683(), class_2246.field_10124.method_9564(), 2);
			}

			class_1473 lv3 = new class_1473(arg);
			class_2338 lv4 = lv.method_11717(0, 2, 0).method_11683();
			lv3.method_5808((double)lv4.method_10263() + 0.5, (double)lv4.method_10264() + 0.05, (double)lv4.method_10260() + 0.5, 0.0F, 0.0F);
			arg.method_8649(lv3);

			for (class_3222 lv5 : arg.method_8403(class_3222.class, lv3.method_5829().method_1014(5.0))) {
				class_174.field_1182.method_9124(lv5, lv3);
			}

			int j = class_2248.method_9507(class_2246.field_10491.method_9564());
			arg.method_8535(2001, lv4, j);
			arg.method_8535(2001, lv4.method_10084(), j);

			for (int k = 0; k < this.method_9729().method_11713(); k++) {
				class_2694 lv6 = lv.method_11717(0, k, 0);
				arg.method_8408(lv6.method_11683(), class_2246.field_10124);
			}
		} else {
			lv = this.method_9730().method_11708(arg, arg2);
			if (lv != null) {
				for (int i = 0; i < this.method_9730().method_11710(); i++) {
					for (int l = 0; l < this.method_9730().method_11713(); l++) {
						arg.method_8652(lv.method_11717(i, l, 0).method_11683(), class_2246.field_10124.method_9564(), 2);
					}
				}

				class_2338 lv7 = lv.method_11717(1, 2, 0).method_11683();
				class_1439 lv8 = new class_1439(arg);
				lv8.method_6499(true);
				lv8.method_5808((double)lv7.method_10263() + 0.5, (double)lv7.method_10264() + 0.05, (double)lv7.method_10260() + 0.5, 0.0F, 0.0F);
				arg.method_8649(lv8);

				for (class_3222 lv5 : arg.method_8403(class_3222.class, lv8.method_5829().method_1014(5.0))) {
					class_174.field_1182.method_9124(lv5, lv8);
				}

				for (int j = 0; j < 120; j++) {
					arg.method_8406(
						class_2398.field_11230,
						(double)lv7.method_10263() + arg.field_9229.nextDouble(),
						(double)lv7.method_10264() + arg.field_9229.nextDouble() * 3.9,
						(double)lv7.method_10260() + arg.field_9229.nextDouble(),
						0.0,
						0.0,
						0.0
					);
				}

				for (int j = 0; j < this.method_9730().method_11710(); j++) {
					for (int k = 0; k < this.method_9730().method_11713(); k++) {
						class_2694 lv6 = lv.method_11717(j, k, 0);
						arg.method_8408(lv6.method_11683(), class_2246.field_10124);
					}
				}
			}
		}
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_10748, arg.method_8042().method_10153());
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10748);
	}

	protected class_2700 method_9732() {
		if (this.field_10749 == null) {
			this.field_10749 = class_2697.method_11701()
				.method_11702(" ", "#", "#")
				.method_11700('#', class_2694.method_11678(class_2715.method_11758(class_2246.field_10491)))
				.method_11704();
		}

		return this.field_10749;
	}

	protected class_2700 method_9729() {
		if (this.field_10750 == null) {
			this.field_10750 = class_2697.method_11701()
				.method_11702("^", "#", "#")
				.method_11700('^', class_2694.method_11678(field_10751))
				.method_11700('#', class_2694.method_11678(class_2715.method_11758(class_2246.field_10491)))
				.method_11704();
		}

		return this.field_10750;
	}

	protected class_2700 method_9727() {
		if (this.field_10752 == null) {
			this.field_10752 = class_2697.method_11701()
				.method_11702("~ ~", "###", "~#~")
				.method_11700('#', class_2694.method_11678(class_2715.method_11758(class_2246.field_10085)))
				.method_11700('~', class_2694.method_11678(class_2710.method_11746(class_3614.field_15959)))
				.method_11704();
		}

		return this.field_10752;
	}

	protected class_2700 method_9730() {
		if (this.field_10753 == null) {
			this.field_10753 = class_2697.method_11701()
				.method_11702("~^~", "###", "~#~")
				.method_11700('^', class_2694.method_11678(field_10751))
				.method_11700('#', class_2694.method_11678(class_2715.method_11758(class_2246.field_10085)))
				.method_11700('~', class_2694.method_11678(class_2710.method_11746(class_3614.field_15959)))
				.method_11704();
		}

		return this.field_10753;
	}
}
