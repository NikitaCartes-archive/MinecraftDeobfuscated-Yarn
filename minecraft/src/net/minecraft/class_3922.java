package net.minecraft;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3922 extends class_2237 implements class_3737 {
	protected static final class_265 field_17351 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
	public static final class_2746 field_17352 = class_2741.field_12548;
	public static final class_2746 field_17353 = class_2741.field_17394;
	public static final class_2746 field_17354 = class_2741.field_12508;
	public static final class_2753 field_17564 = class_2741.field_12481;

	public class_3922(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_17352, Boolean.valueOf(true))
				.method_11657(field_17353, Boolean.valueOf(false))
				.method_11657(field_17354, Boolean.valueOf(false))
				.method_11657(field_17564, class_2350.field_11043)
		);
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		if ((Boolean)arg.method_11654(field_17352)) {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_3924) {
				class_3924 lv2 = (class_3924)lv;
				class_1799 lv3 = arg4.method_5998(arg5);
				Optional<class_3920> optional = lv2.method_17502(lv3);
				if (optional.isPresent()) {
					if (!arg2.field_9236 && lv2.method_17503(arg4.field_7503.field_7477 ? lv3.method_7972() : lv3, ((class_3920)optional.get()).method_8167())) {
						arg4.method_7281(class_3468.field_17486);
					}

					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		if (!arg4.method_5753() && (Boolean)arg.method_11654(field_17352) && arg4 instanceof class_1309 && !class_1890.method_8216((class_1309)arg4)) {
			arg4.method_5643(class_1282.field_5867, 1.0F);
		}

		super.method_9548(arg, arg2, arg3, arg4);
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg4.method_11614()) {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_3924) {
				class_1264.method_17349(arg2, arg3, ((class_3924)lv).method_17505());
			}

			super.method_9536(arg, arg2, arg3, arg4, bl);
		}
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_1936 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		boolean bl = lv.method_8316(lv2).method_15772() == class_3612.field_15910;
		return this.method_9564()
			.method_11657(field_17354, Boolean.valueOf(bl))
			.method_11657(field_17353, Boolean.valueOf(this.method_17456(lv.method_8320(lv2.method_10074()))))
			.method_11657(field_17352, Boolean.valueOf(!bl))
			.method_11657(field_17564, arg.method_8042());
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if ((Boolean)arg.method_11654(field_17354)) {
			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
		}

		return arg2 == class_2350.field_11033
			? arg.method_11657(field_17353, Boolean.valueOf(this.method_17456(arg3)))
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	private boolean method_17456(class_2680 arg) {
		return arg.method_11614() == class_2246.field_10359;
	}

	@Override
	public int method_9593(class_2680 arg) {
		return arg.method_11654(field_17352) ? super.method_9593(arg) : 0;
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_17351;
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11458;
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if ((Boolean)arg.method_11654(field_17352)) {
			if (random.nextInt(10) == 0) {
				arg2.method_8486(
					(double)((float)arg3.method_10263() + 0.5F),
					(double)((float)arg3.method_10264() + 0.5F),
					(double)((float)arg3.method_10260() + 0.5F),
					class_3417.field_17483,
					class_3419.field_15245,
					0.5F + random.nextFloat(),
					random.nextFloat() * 0.7F + 0.6F,
					false
				);
			}

			if (random.nextInt(5) == 0) {
				for (int i = 0; i < random.nextInt(1) + 1; i++) {
					arg2.method_8406(
						class_2398.field_11239,
						(double)((float)arg3.method_10263() + 0.5F),
						(double)((float)arg3.method_10264() + 0.5F),
						(double)((float)arg3.method_10260() + 0.5F),
						(double)(random.nextFloat() / 2.0F),
						5.0E-5,
						(double)(random.nextFloat() / 2.0F)
					);
				}
			}
		}
	}

	@Override
	public boolean method_10311(class_1936 arg, class_2338 arg2, class_2680 arg3, class_3610 arg4) {
		if (!(Boolean)arg3.method_11654(class_2741.field_12508) && arg4.method_15772() == class_3612.field_15910) {
			boolean bl = (Boolean)arg3.method_11654(field_17352);
			if (bl) {
				if (arg.method_8608()) {
					for (int i = 0; i < 20; i++) {
						method_17455(arg.method_8410(), arg2, (Boolean)arg3.method_11654(field_17353), true);
					}
				} else {
					arg.method_8396(null, arg2, class_3417.field_15222, class_3419.field_15245, 1.0F, 1.0F);
				}

				class_2586 lv = arg.method_8321(arg2);
				if (lv instanceof class_3924) {
					((class_3924)lv).method_17506();
				}
			}

			arg.method_8652(arg2, arg3.method_11657(field_17354, Boolean.valueOf(true)).method_11657(field_17352, Boolean.valueOf(false)), 3);
			arg.method_8405().method_8676(arg2, arg4.method_15772(), arg4.method_15772().method_15789(arg));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void method_19286(class_1937 arg, class_2680 arg2, class_3965 arg3, class_1297 arg4) {
		if (!arg.field_9236 && arg4 instanceof class_1665) {
			class_1665 lv = (class_1665)arg4;
			if (lv.method_5809() && !(Boolean)arg2.method_11654(field_17352) && !(Boolean)arg2.method_11654(field_17354)) {
				class_2338 lv2 = arg3.method_17777();
				arg.method_8652(lv2, arg2.method_11657(class_2741.field_12548, Boolean.valueOf(true)), 11);
			}
		}
	}

	public static void method_17455(class_1937 arg, class_2338 arg2, boolean bl, boolean bl2) {
		Random random = arg.method_8409();
		class_2400 lv = bl ? class_2398.field_17431 : class_2398.field_17430;
		arg.method_17452(
			lv,
			true,
			(double)arg2.method_10263() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1),
			(double)arg2.method_10264() + random.nextDouble() + random.nextDouble(),
			(double)arg2.method_10260() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1),
			0.0,
			0.07,
			0.0
		);
		if (bl2) {
			arg.method_8406(
				class_2398.field_11251,
				(double)arg2.method_10263() + 0.25 + random.nextDouble() / 2.0 * (double)(random.nextBoolean() ? 1 : -1),
				(double)arg2.method_10264() + 0.4,
				(double)arg2.method_10260() + 0.25 + random.nextDouble() / 2.0 * (double)(random.nextBoolean() ? 1 : -1),
				0.0,
				0.005,
				0.0
			);
		}
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		return arg.method_11654(field_17354) ? class_3612.field_15910.method_15729(false) : super.method_9545(arg);
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_17564, arg2.method_10503(arg.method_11654(field_17564)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_17564)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_17352, field_17353, field_17354, field_17564);
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_3924();
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
