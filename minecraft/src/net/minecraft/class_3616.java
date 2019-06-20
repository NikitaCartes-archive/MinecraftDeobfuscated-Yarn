package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_3616 extends class_3609 {
	@Override
	public class_3611 method_15750() {
		return class_3612.field_15907;
	}

	@Override
	public class_3611 method_15751() {
		return class_3612.field_15908;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1921 method_15786() {
		return class_1921.field_9178;
	}

	@Override
	public class_1792 method_15774() {
		return class_1802.field_8187;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_15776(class_1937 arg, class_2338 arg2, class_3610 arg3, Random random) {
		class_2338 lv = arg2.method_10084();
		if (arg.method_8320(lv).method_11588() && !arg.method_8320(lv).method_11598(arg, lv)) {
			if (random.nextInt(100) == 0) {
				double d = (double)((float)arg2.method_10263() + random.nextFloat());
				double e = (double)(arg2.method_10264() + 1);
				double f = (double)((float)arg2.method_10260() + random.nextFloat());
				arg.method_8406(class_2398.field_11239, d, e, f, 0.0, 0.0, 0.0);
				arg.method_8486(d, e, f, class_3417.field_14576, class_3419.field_15245, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}

			if (random.nextInt(200) == 0) {
				arg.method_8486(
					(double)arg2.method_10263(),
					(double)arg2.method_10264(),
					(double)arg2.method_10260(),
					class_3417.field_15021,
					class_3419.field_15245,
					0.2F + random.nextFloat() * 0.2F,
					0.9F + random.nextFloat() * 0.15F,
					false
				);
			}
		}
	}

	@Override
	public void method_15792(class_1937 arg, class_2338 arg2, class_3610 arg3, Random random) {
		if (arg.method_8450().method_8355(class_1928.field_19387)) {
			int i = random.nextInt(3);
			if (i > 0) {
				class_2338 lv = arg2;

				for (int j = 0; j < i; j++) {
					lv = lv.method_10069(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
					if (!arg.method_8477(lv)) {
						return;
					}

					class_2680 lv2 = arg.method_8320(lv);
					if (lv2.method_11588()) {
						if (this.method_15819(arg, lv)) {
							arg.method_8501(lv, class_2246.field_10036.method_9564());
							return;
						}
					} else if (lv2.method_11620().method_15801()) {
						return;
					}
				}
			} else {
				for (int k = 0; k < 3; k++) {
					class_2338 lv3 = arg2.method_10069(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
					if (!arg.method_8477(lv3)) {
						return;
					}

					if (arg.method_8623(lv3.method_10084()) && this.method_15817(arg, lv3)) {
						arg.method_8501(lv3.method_10084(), class_2246.field_10036.method_9564());
					}
				}
			}
		}
	}

	private boolean method_15819(class_1941 arg, class_2338 arg2) {
		for (class_2350 lv : class_2350.values()) {
			if (this.method_15817(arg, arg2.method_10093(lv))) {
				return true;
			}
		}

		return false;
	}

	private boolean method_15817(class_1941 arg, class_2338 arg2) {
		return arg2.method_10264() >= 0 && arg2.method_10264() < 256 && !arg.method_8591(arg2) ? false : arg.method_8320(arg2).method_11620().method_15802();
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	@Override
	public class_2394 method_15787() {
		return class_2398.field_11223;
	}

	@Override
	protected void method_15730(class_1936 arg, class_2338 arg2, class_2680 arg3) {
		this.method_15818(arg, arg2);
	}

	@Override
	public int method_15733(class_1941 arg) {
		return arg.method_8597().method_12465() ? 4 : 2;
	}

	@Override
	public class_2680 method_15790(class_3610 arg) {
		return class_2246.field_10164.method_9564().method_11657(class_2404.field_11278, Integer.valueOf(method_15741(arg)));
	}

	@Override
	public boolean method_15780(class_3611 arg) {
		return arg == class_3612.field_15908 || arg == class_3612.field_15907;
	}

	@Override
	public int method_15739(class_1941 arg) {
		return arg.method_8597().method_12465() ? 1 : 2;
	}

	@Override
	public boolean method_15777(class_3610 arg, class_1922 arg2, class_2338 arg3, class_3611 arg4, class_2350 arg5) {
		return arg.method_15763(arg2, arg3) >= 0.44444445F && arg4.method_15791(class_3486.field_15517);
	}

	@Override
	public int method_15789(class_1941 arg) {
		return arg.method_8597().method_12467() ? 10 : 30;
	}

	@Override
	public int method_15753(class_1937 arg, class_2338 arg2, class_3610 arg3, class_3610 arg4) {
		int i = this.method_15789(arg);
		if (!arg3.method_15769()
			&& !arg4.method_15769()
			&& !(Boolean)arg3.method_11654(field_15902)
			&& !(Boolean)arg4.method_11654(field_15902)
			&& arg4.method_15763(arg, arg2) > arg3.method_15763(arg, arg2)
			&& arg.method_8409().nextInt(4) != 0) {
			i *= 4;
		}

		return i;
	}

	private void method_15818(class_1936 arg, class_2338 arg2) {
		arg.method_20290(1501, arg2, 0);
	}

	@Override
	protected boolean method_15737() {
		return false;
	}

	@Override
	protected void method_15745(class_1936 arg, class_2338 arg2, class_2680 arg3, class_2350 arg4, class_3610 arg5) {
		if (arg4 == class_2350.field_11033) {
			class_3610 lv = arg.method_8316(arg2);
			if (this.method_15791(class_3486.field_15518) && lv.method_15767(class_3486.field_15517)) {
				if (arg3.method_11614() instanceof class_2404) {
					arg.method_8652(arg2, class_2246.field_10340.method_9564(), 3);
				}

				this.method_15818(arg, arg2);
				return;
			}
		}

		super.method_15745(arg, arg2, arg3, arg4, arg5);
	}

	@Override
	protected boolean method_15795() {
		return true;
	}

	@Override
	protected float method_15784() {
		return 100.0F;
	}

	public static class class_3617 extends class_3616 {
		@Override
		protected void method_15775(class_2689.class_2690<class_3611, class_3610> arg) {
			super.method_15775(arg);
			arg.method_11667(field_15900);
		}

		@Override
		public int method_15779(class_3610 arg) {
			return (Integer)arg.method_11654(field_15900);
		}

		@Override
		public boolean method_15793(class_3610 arg) {
			return false;
		}
	}

	public static class class_3618 extends class_3616 {
		@Override
		public int method_15779(class_3610 arg) {
			return 8;
		}

		@Override
		public boolean method_15793(class_3610 arg) {
			return true;
		}
	}
}
