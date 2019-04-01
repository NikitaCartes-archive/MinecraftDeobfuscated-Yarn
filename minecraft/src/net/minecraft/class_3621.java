package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_3621 extends class_3609 {
	@Override
	public class_3611 method_15750() {
		return class_3612.field_15909;
	}

	@Override
	public class_3611 method_15751() {
		return class_3612.field_15910;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1921 method_15786() {
		return class_1921.field_9179;
	}

	@Override
	public class_1792 method_15774() {
		return class_1802.field_8705;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_15776(class_1937 arg, class_2338 arg2, class_3610 arg3, Random random) {
		if (!arg3.method_15771() && !(Boolean)arg3.method_11654(field_15902)) {
			if (random.nextInt(64) == 0) {
				arg.method_8486(
					(double)arg2.method_10263() + 0.5,
					(double)arg2.method_10264() + 0.5,
					(double)arg2.method_10260() + 0.5,
					class_3417.field_15237,
					class_3419.field_15245,
					random.nextFloat() * 0.25F + 0.75F,
					random.nextFloat() + 0.5F,
					false
				);
			}
		} else if (random.nextInt(10) == 0) {
			arg.method_8406(
				class_2398.field_11210,
				(double)((float)arg2.method_10263() + random.nextFloat()),
				(double)((float)arg2.method_10264() + random.nextFloat()),
				(double)((float)arg2.method_10260() + random.nextFloat()),
				0.0,
				0.0,
				0.0
			);
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	@Override
	public class_2394 method_15787() {
		return class_2398.field_11232;
	}

	@Override
	protected boolean method_15737() {
		return true;
	}

	@Override
	protected void method_15730(class_1936 arg, class_2338 arg2, class_2680 arg3) {
		class_2586 lv = arg3.method_11614().method_9570() ? arg.method_8321(arg2) : null;
		class_2248.method_9610(arg3, arg.method_8410(), arg2, lv);
	}

	@Override
	public int method_15733(class_1941 arg) {
		return 4;
	}

	@Override
	public class_2680 method_15790(class_3610 arg) {
		return class_2246.field_10382.method_9564().method_11657(class_2404.field_11278, Integer.valueOf(method_15741(arg)));
	}

	@Override
	public boolean method_15780(class_3611 arg) {
		return arg == class_3612.field_15910 || arg == class_3612.field_15909;
	}

	@Override
	public int method_15739(class_1941 arg) {
		return 1;
	}

	@Override
	public int method_15789(class_1941 arg) {
		return 5;
	}

	@Override
	public boolean method_15777(class_3610 arg, class_1922 arg2, class_2338 arg3, class_3611 arg4, class_2350 arg5) {
		return arg5 == class_2350.field_11033 && !arg4.method_15791(class_3486.field_15517);
	}

	@Override
	protected float method_15784() {
		return 100.0F;
	}

	public static class class_3622 extends class_3621 {
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

	public static class class_3623 extends class_3621 {
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
