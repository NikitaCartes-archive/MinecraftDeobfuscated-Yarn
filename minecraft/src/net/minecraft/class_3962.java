package net.minecraft;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3962 extends class_2248 implements class_3954 {
	public static final class_2758 field_17565 = class_2741.field_17586;
	public static final Object2FloatMap<class_1935> field_17566 = new Object2FloatOpenHashMap<>();
	public static final class_265 field_17567 = class_259.method_1077();
	private static final class_265[] field_17568 = class_156.method_654(new class_265[9], args -> {
		for (int i = 0; i < 8; i++) {
			args[i] = class_259.method_1072(field_17567, class_2248.method_9541(2.0, (double)Math.max(2, 1 + i * 2), 2.0, 14.0, 16.0, 14.0), class_247.field_16886);
		}

		args[8] = args[7];
	});

	public static void method_17758() {
		field_17566.defaultReturnValue(-1.0F);
		float f = 0.3F;
		float g = 0.5F;
		float h = 0.65F;
		float i = 0.85F;
		float j = 1.0F;
		method_17753(0.3F, class_1802.field_17506);
		method_17753(0.3F, class_1802.field_17503);
		method_17753(0.3F, class_1802.field_17504);
		method_17753(0.3F, class_1802.field_17508);
		method_17753(0.3F, class_1802.field_17507);
		method_17753(0.3F, class_1802.field_17505);
		method_17753(0.3F, class_1802.field_17535);
		method_17753(0.3F, class_1802.field_17536);
		method_17753(0.3F, class_1802.field_17537);
		method_17753(0.3F, class_1802.field_17538);
		method_17753(0.3F, class_1802.field_17539);
		method_17753(0.3F, class_1802.field_17540);
		method_17753(0.3F, class_1802.field_8309);
		method_17753(0.3F, class_1802.field_8551);
		method_17753(0.3F, class_1802.field_8602);
		method_17753(0.3F, class_1802.field_17532);
		method_17753(0.3F, class_1802.field_8188);
		method_17753(0.3F, class_1802.field_8706);
		method_17753(0.3F, class_1802.field_8158);
		method_17753(0.3F, class_1802.field_16998);
		method_17753(0.3F, class_1802.field_8317);
		method_17753(0.5F, class_1802.field_17533);
		method_17753(0.5F, class_1802.field_8256);
		method_17753(0.5F, class_1802.field_17520);
		method_17753(0.5F, class_1802.field_17531);
		method_17753(0.5F, class_1802.field_17523);
		method_17753(0.5F, class_1802.field_8497);
		method_17753(0.65F, class_1802.field_17498);
		method_17753(0.65F, class_1802.field_17524);
		method_17753(0.65F, class_1802.field_17518);
		method_17753(0.65F, class_1802.field_17519);
		method_17753(0.65F, class_1802.field_17522);
		method_17753(0.65F, class_1802.field_8279);
		method_17753(0.65F, class_1802.field_8186);
		method_17753(0.65F, class_1802.field_8179);
		method_17753(0.65F, class_1802.field_8116);
		method_17753(0.65F, class_1802.field_8567);
		method_17753(0.65F, class_1802.field_8861);
		method_17753(0.65F, class_1802.field_17516);
		method_17753(0.65F, class_1802.field_17517);
		method_17753(0.65F, class_1802.field_17521);
		method_17753(0.65F, class_1802.field_8491);
		method_17753(0.65F, class_1802.field_8880);
		method_17753(0.65F, class_1802.field_17499);
		method_17753(0.65F, class_1802.field_17500);
		method_17753(0.65F, class_1802.field_17501);
		method_17753(0.65F, class_1802.field_17502);
		method_17753(0.65F, class_1802.field_17509);
		method_17753(0.65F, class_1802.field_17510);
		method_17753(0.65F, class_1802.field_17511);
		method_17753(0.65F, class_1802.field_17512);
		method_17753(0.65F, class_1802.field_17513);
		method_17753(0.65F, class_1802.field_17514);
		method_17753(0.65F, class_1802.field_17515);
		method_17753(0.65F, class_1802.field_8471);
		method_17753(0.65F, class_1802.field_17525);
		method_17753(0.65F, class_1802.field_17526);
		method_17753(0.65F, class_1802.field_17527);
		method_17753(0.65F, class_1802.field_17529);
		method_17753(0.65F, class_1802.field_8561);
		method_17753(0.85F, class_1802.field_17528);
		method_17753(0.85F, class_1802.field_8506);
		method_17753(0.85F, class_1802.field_8682);
		method_17753(0.85F, class_1802.field_8229);
		method_17753(0.85F, class_1802.field_8512);
		method_17753(0.85F, class_1802.field_8423);
		method_17753(1.0F, class_1802.field_17534);
		method_17753(1.0F, class_1802.field_8741);
	}

	private static void method_17753(float f, class_1935 arg) {
		field_17566.put(arg.method_8389(), f);
	}

	public class_3962(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_17565, Integer.valueOf(0)));
	}

	@Environment(EnvType.CLIENT)
	public static void method_18027(class_1937 arg, class_2338 arg2, boolean bl) {
		class_2680 lv = arg.method_8320(arg2);
		arg.method_8486(
			(double)arg2.method_10263(),
			(double)arg2.method_10264(),
			(double)arg2.method_10260(),
			bl ? class_3417.field_17608 : class_3417.field_17607,
			class_3419.field_15245,
			1.0F,
			1.0F,
			false
		);
		double d = lv.method_17770(arg, arg2).method_1102(class_2350.class_2351.field_11052, 0.5, 0.5) + 0.03125;
		double e = 0.13125F;
		double f = 0.7375F;
		Random random = arg.method_8409();

		for (int i = 0; i < 10; i++) {
			double g = random.nextGaussian() * 0.02;
			double h = random.nextGaussian() * 0.02;
			double j = random.nextGaussian() * 0.02;
			arg.method_8406(
				class_2398.field_17741,
				(double)arg2.method_10263() + 0.13125F + 0.7375F * (double)random.nextFloat(),
				(double)arg2.method_10264() + d + (double)random.nextFloat() * (1.0 - d),
				(double)arg2.method_10260() + 0.13125F + 0.7375F * (double)random.nextFloat(),
				g,
				h,
				j
			);
		}
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_17568[arg.method_11654(field_17565)];
	}

	@Override
	public class_265 method_9584(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_17567;
	}

	@Override
	public class_265 method_9549(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_17568[0];
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if ((Integer)arg.method_11654(field_17565) == 7) {
			arg2.method_8397().method_8676(arg3, arg.method_11614(), 20);
		}
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		int i = (Integer)arg.method_11654(field_17565);
		class_1799 lv = arg4.method_5998(arg5);
		if (i < 8 && field_17566.containsKey(lv.method_7909())) {
			if (i < 7 && !arg2.field_9236) {
				boolean bl = method_17756(arg, arg2, arg3, lv);
				arg2.method_8535(1500, arg3, bl ? 1 : 0);
				if (!arg4.field_7503.field_7477) {
					lv.method_7934(1);
				}
			}

			return true;
		} else if (i == 8) {
			if (!arg2.field_9236) {
				float f = 0.7F;
				double d = (double)(arg2.field_9229.nextFloat() * 0.7F) + 0.15F;
				double e = (double)(arg2.field_9229.nextFloat() * 0.7F) + 0.060000002F + 0.6;
				double g = (double)(arg2.field_9229.nextFloat() * 0.7F) + 0.15F;
				class_1542 lv2 = new class_1542(
					arg2, (double)arg3.method_10263() + d, (double)arg3.method_10264() + e, (double)arg3.method_10260() + g, new class_1799(class_1802.field_8324)
				);
				lv2.method_6988();
				arg2.method_8649(lv2);
			}

			method_17759(arg, arg2, arg3);
			arg2.method_8396(null, arg3, class_3417.field_17606, class_3419.field_15245, 1.0F, 1.0F);
			return true;
		} else {
			return false;
		}
	}

	private static void method_17759(class_2680 arg, class_1936 arg2, class_2338 arg3) {
		arg2.method_8652(arg3, arg.method_11657(field_17565, Integer.valueOf(0)), 3);
	}

	private static boolean method_17756(class_2680 arg, class_1936 arg2, class_2338 arg3, class_1799 arg4) {
		int i = (Integer)arg.method_11654(field_17565);
		float f = field_17566.getFloat(arg4.method_7909());
		if ((i != 0 || !(f > 0.0F)) && !(arg2.method_8409().nextDouble() < (double)f)) {
			return false;
		} else {
			int j = i + 1;
			arg2.method_8652(arg3, arg.method_11657(field_17565, Integer.valueOf(j)), 3);
			if (j == 7) {
				arg2.method_8397().method_8676(arg3, arg.method_11614(), 20);
			}

			return true;
		}
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if ((Integer)arg.method_11654(field_17565) == 7) {
			arg2.method_8652(arg3, arg.method_11572(field_17565), 3);
			arg2.method_8396(null, arg3, class_3417.field_17609, class_3419.field_15245, 1.0F, 1.0F);
		}

		super.method_9588(arg, arg2, arg3, random);
	}

	@Override
	public boolean method_9498(class_2680 arg) {
		return true;
	}

	@Override
	public int method_9572(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return (Integer)arg.method_11654(field_17565);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_17565);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}

	@Override
	public class_1278 method_17680(class_2680 arg, class_1936 arg2, class_2338 arg3) {
		int i = (Integer)arg.method_11654(field_17565);
		if (i == 8) {
			return new class_3962.class_3964(arg, arg2, arg3, new class_1799(class_1802.field_8324));
		} else {
			return (class_1278)(i < 7 ? new class_3962.class_3963(arg, arg2, arg3) : new class_3962.class_3925());
		}
	}

	static class class_3925 extends class_1277 implements class_1278 {
		public class_3925() {
			super(0);
		}

		@Override
		public int[] method_5494(class_2350 arg) {
			return new int[0];
		}

		@Override
		public boolean method_5492(int i, class_1799 arg, @Nullable class_2350 arg2) {
			return false;
		}

		@Override
		public boolean method_5493(int i, class_1799 arg, class_2350 arg2) {
			return false;
		}
	}

	static class class_3963 extends class_1277 implements class_1278 {
		private final class_2680 field_17569;
		private final class_1936 field_17570;
		private final class_2338 field_17571;
		private boolean field_17572;

		public class_3963(class_2680 arg, class_1936 arg2, class_2338 arg3) {
			super(1);
			this.field_17569 = arg;
			this.field_17570 = arg2;
			this.field_17571 = arg3;
		}

		@Override
		public int method_5444() {
			return 1;
		}

		@Override
		public int[] method_5494(class_2350 arg) {
			return arg == class_2350.field_11036 ? new int[]{0} : new int[0];
		}

		@Override
		public boolean method_5492(int i, class_1799 arg, @Nullable class_2350 arg2) {
			return !this.field_17572 && arg2 == class_2350.field_11036 && class_3962.field_17566.containsKey(arg.method_7909());
		}

		@Override
		public boolean method_5493(int i, class_1799 arg, class_2350 arg2) {
			return false;
		}

		@Override
		public void method_5431() {
			class_1799 lv = this.method_5438(0);
			if (!lv.method_7960()) {
				this.field_17572 = true;
				class_3962.method_17756(this.field_17569, this.field_17570, this.field_17571, lv);
				this.method_5441(0);
			}
		}
	}

	static class class_3964 extends class_1277 implements class_1278 {
		private final class_2680 field_17573;
		private final class_1936 field_17574;
		private final class_2338 field_17575;
		private boolean field_17576;

		public class_3964(class_2680 arg, class_1936 arg2, class_2338 arg3, class_1799 arg4) {
			super(arg4);
			this.field_17573 = arg;
			this.field_17574 = arg2;
			this.field_17575 = arg3;
		}

		@Override
		public int method_5444() {
			return 1;
		}

		@Override
		public int[] method_5494(class_2350 arg) {
			return arg == class_2350.field_11033 ? new int[]{0} : new int[0];
		}

		@Override
		public boolean method_5492(int i, class_1799 arg, @Nullable class_2350 arg2) {
			return false;
		}

		@Override
		public boolean method_5493(int i, class_1799 arg, class_2350 arg2) {
			return !this.field_17576 && arg2 == class_2350.field_11033 && arg.method_7909() == class_1802.field_8324;
		}

		@Override
		public void method_5431() {
			class_3962.method_17759(this.field_17573, this.field_17574, this.field_17575);
			this.field_17576 = true;
		}
	}
}
