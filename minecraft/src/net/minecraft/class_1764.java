package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;

public class class_1764 extends class_1811 {
	private boolean field_7937 = false;
	private boolean field_7936 = false;

	public class_1764(class_1792.class_1793 arg) {
		super(arg);
		this.method_7863(new class_2960("pull"), (argx, arg2, arg3) -> {
			if (arg3 == null || argx.method_7909() != this) {
				return 0.0F;
			} else {
				return method_7781(argx) ? 0.0F : (float)(argx.method_7935() - arg3.method_6014()) / (float)method_7775(argx);
			}
		});
		this.method_7863(
			new class_2960("pulling"), (argx, arg2, arg3) -> arg3 != null && arg3.method_6115() && arg3.method_6030() == argx && !method_7781(argx) ? 1.0F : 0.0F
		);
		this.method_7863(new class_2960("charged"), (argx, arg2, arg3) -> arg3 != null && method_7781(argx) ? 1.0F : 0.0F);
		this.method_7863(
			new class_2960("firework"), (argx, arg2, arg3) -> arg3 != null && method_7781(argx) && this.method_7772(argx, class_1802.field_8639) ? 1.0F : 0.0F
		);
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		boolean bl = !this.method_7774(arg2).method_7960();
		class_1799 lv2 = this.method_8008(arg2);
		if (method_7781(lv)) {
			this.method_7777(arg, arg2, lv);
			method_7782(lv, false);
			return new class_1271<>(class_1269.field_5812, lv);
		} else if (!arg2.field_7503.field_7477 && lv2.method_7960() && !bl) {
			return lv2.method_7960() && !bl ? new class_1271<>(class_1269.field_5814, lv) : new class_1271<>(class_1269.field_5811, lv);
		} else {
			if (!method_7781(lv)) {
				this.field_7937 = false;
				this.field_7936 = false;
				arg2.method_6019(arg3);
			}

			return new class_1271<>(class_1269.field_5812, lv);
		}
	}

	@Override
	public void method_7840(class_1799 arg, class_1937 arg2, class_1309 arg3, int i) {
		if (arg3 instanceof class_1657) {
			int j = this.method_7881(arg) - i;
			float f = method_7770(j, arg);
			if (f >= 1.0F && !method_7781(arg)) {
				this.method_7767((class_1657)arg3, arg);
				method_7782(arg, true);
				arg2.method_8465(
					null,
					arg3.field_5987,
					arg3.field_6010,
					arg3.field_6035,
					class_3417.field_14626,
					class_3419.field_15248,
					1.0F,
					1.0F / (field_8005.nextFloat() * 0.5F + 1.0F) + 0.2F
				);
			}
		}
	}

	private void method_7767(class_1657 arg, class_1799 arg2) {
		class_1799 lv = this.method_7774(arg);
		boolean bl = arg.field_7503.field_7477;
		int i = class_1890.method_8225(class_1893.field_9108, arg2);
		int j = i == 0 ? 1 : 3;
		class_1799 lv2 = lv.method_7960() ? this.method_8008(arg) : lv;
		class_1799 lv3 = lv2.method_7972();

		for (int k = 0; k < j; k++) {
			if (k > 0) {
				lv2 = lv3.method_7972();
			}

			if (lv2.method_7960() && bl) {
				lv2 = new class_1799(class_1802.field_8107);
				lv3 = lv2.method_7972();
			}

			this.method_7765(arg, arg2, lv2, k > 0);
		}
	}

	private void method_7765(class_1657 arg, class_1799 arg2, class_1799 arg3, boolean bl) {
		boolean bl2 = arg.field_7503.field_7477;
		boolean bl3 = bl2 && arg3.method_7909() == class_1802.field_8107;
		class_1799 lv;
		if (!bl3 && !bl2 && !bl) {
			lv = arg3.method_7971(1);
			if (arg3.method_7960()) {
				arg.field_7514.method_7378(arg3);
			}
		} else {
			lv = arg3.method_7972();
		}

		this.method_7778(arg2, lv);
	}

	public static boolean method_7781(class_1799 arg) {
		class_2487 lv = arg.method_7969();
		return lv != null ? lv.method_10577("Charged") : false;
	}

	public static class_2487 method_7782(class_1799 arg, boolean bl) {
		class_2487 lv = arg.method_7948();
		lv.method_10556("Charged", bl);
		return lv;
	}

	private void method_7778(class_1799 arg, class_1799 arg2) {
		class_2487 lv = arg.method_7948();
		class_2499 lv2;
		if (lv.method_10573("ChargedProjectiles", 9)) {
			lv2 = lv.method_10554("ChargedProjectiles", 10);
		} else {
			lv2 = new class_2499();
		}

		class_2487 lv3 = new class_2487();
		arg2.method_7953(lv3);
		lv2.method_10606(lv3);
		lv.method_10566("ChargedProjectiles", lv2);
	}

	private List<class_1799> method_7785(class_1799 arg) {
		List<class_1799> list = Lists.<class_1799>newArrayList();
		class_2487 lv = arg.method_7969();
		if (lv != null && lv.method_10573("ChargedProjectiles", 9)) {
			class_2499 lv2 = lv.method_10554("ChargedProjectiles", 10);
			if (lv2 != null) {
				for (int i = 0; i < lv2.size(); i++) {
					class_2487 lv3 = lv2.method_10602(i);
					list.add(class_1799.method_7915(lv3));
				}
			}
		}

		return list;
	}

	private void method_7766(class_1799 arg) {
		class_2487 lv = arg.method_7969();
		if (lv != null) {
			class_2499 lv2 = lv.method_10554("ChargedProjectiles", 9);
			lv2.clear();
			lv.method_10566("ChargedProjectiles", lv2);
		}
	}

	private boolean method_7772(class_1799 arg, class_1792 arg2) {
		return this.method_7785(arg).stream().anyMatch(arg2x -> arg2x.method_7909() == arg2);
	}

	private void method_7763(class_1937 arg, class_1657 arg2, class_1799 arg3, class_1799 arg4, float f, float g, boolean bl) {
		if (!this.method_7764(arg, arg2, arg3, arg4, f) && !arg4.method_7960()) {
			boolean bl2 = arg2.field_7503.field_7477;
			boolean bl3 = bl2 && arg4.method_7909() == class_1802.field_8107;
			if (!arg.field_9236) {
				class_1744 lv = (class_1744)(arg4.method_7909() instanceof class_1744 ? arg4.method_7909() : class_1802.field_8107);
				class_1665 lv2 = lv.method_7702(arg, arg4, arg2);
				lv2.method_7437(arg2, arg2.field_5965, f, 0.0F, 3.15F, 1.0F);
				lv2.method_7439(true);
				lv2.method_7444(class_3417.field_14636);
				lv2.method_7442(true);
				int i = class_1890.method_8225(class_1893.field_9132, arg3);
				if (i > 0) {
					lv2.method_7451((byte)i);
				}

				arg3.method_7956(1, arg2);
				if (bl3 || bl || arg2.field_7503.field_7477 && (arg4.method_7909() == class_1802.field_8236 || arg4.method_7909() == class_1802.field_8087)) {
					lv2.field_7572 = class_1665.class_1666.field_7594;
				}

				arg.method_8649(lv2);
			}

			arg.method_8465(null, arg2.field_5987, arg2.field_6010, arg2.field_6035, class_3417.field_15187, class_3419.field_15248, 1.0F, g);
		}
	}

	private boolean method_7764(class_1937 arg, class_1657 arg2, class_1799 arg3, class_1799 arg4, float f) {
		if (arg4.method_7909() != class_1802.field_8639) {
			return false;
		} else if (!arg4.method_7960()) {
			if (!arg.field_9236) {
				class_1671 lv = new class_1671(arg, arg4, arg2.field_5987, arg2.field_6010 + (double)arg2.method_5751() - 0.15F, arg2.field_6035, true);
				lv.method_7474(arg2, f, 1.0F);
				arg3.method_7956(3, arg2);
				arg.method_8649(lv);
				arg.method_8465(null, arg2.field_5987, arg2.field_6010, arg2.field_6035, class_3417.field_15187, class_3419.field_15248, 1.0F, 1.0F);
			}

			this.method_7769(arg, arg2, arg3);
			return true;
		} else {
			return false;
		}
	}

	private void method_7777(class_1937 arg, class_1657 arg2, class_1799 arg3) {
		List<class_1799> list = this.method_7785(arg3);
		float f = 10.0F;
		float[] fs = this.method_7780(arg2.method_6051());

		for (int i = 0; i < list.size(); i++) {
			class_1799 lv = (class_1799)list.get(i);
			if (!lv.method_7960()) {
				if (i == 0) {
					this.method_7763(arg, arg2, arg3, lv, arg2.field_6031, fs[i], false);
				} else if (i == 1) {
					this.method_7763(arg, arg2, arg3, lv, arg2.field_6031 - 10.0F, fs[i], true);
				} else if (i == 2) {
					this.method_7763(arg, arg2, arg3, lv, arg2.field_6031 + 10.0F, fs[i], true);
				}
			}
		}

		this.method_7769(arg, arg2, arg3);
	}

	private float[] method_7780(Random random) {
		boolean bl = random.nextBoolean();
		return new float[]{1.0F, this.method_7784(bl), this.method_7784(!bl)};
	}

	private float method_7784(boolean bl) {
		float f = bl ? 0.63F : 0.43F;
		return 1.0F / (field_8005.nextFloat() * 0.5F + 1.8F) + f;
	}

	private void method_7769(class_1937 arg, class_1657 arg2, class_1799 arg3) {
		if (!arg.field_9236) {
			class_174.field_1196.method_9115((class_3222)arg2, arg3);
		}

		arg2.method_7259(class_3468.field_15372.method_14956(this));
		this.method_7766(arg3);
	}

	@Override
	public void method_7852(class_1937 arg, class_1309 arg2, class_1799 arg3, int i) {
		if (!arg.field_9236) {
			int j = class_1890.method_8225(class_1893.field_9098, arg3);
			class_3414 lv = this.method_7773(j);
			class_3414 lv2 = j == 0 ? class_3417.field_14860 : null;
			float f = (float)(arg3.method_7935() - i) / (float)method_7775(arg3);
			if (f < 0.2F) {
				this.field_7937 = false;
				this.field_7936 = false;
			}

			if (f >= 0.2F && !this.field_7937) {
				this.field_7937 = true;
				arg.method_8465(null, arg2.field_5987, arg2.field_6010, arg2.field_6035, lv, class_3419.field_15248, 0.5F, 1.0F);
			}

			if (f >= 0.5F && lv2 != null && !this.field_7936) {
				this.field_7936 = true;
				arg.method_8465(null, arg2.field_5987, arg2.field_6010, arg2.field_6035, lv2, class_3419.field_15248, 0.5F, 1.0F);
			}
		}
	}

	@Override
	public int method_7881(class_1799 arg) {
		return method_7775(arg) + 3;
	}

	public static int method_7775(class_1799 arg) {
		int i = class_1890.method_8225(class_1893.field_9098, arg);
		return i == 0 ? 25 : 25 - 5 * i;
	}

	@Override
	public class_1839 method_7853(class_1799 arg) {
		return class_1839.field_8947;
	}

	private class_1799 method_7774(class_1657 arg) {
		class_1799 lv = arg.method_6079();
		if (!lv.method_7960() && lv.method_7909() instanceof class_1781) {
			return lv;
		} else {
			class_1799 lv2 = arg.method_6047();
			return !lv2.method_7960() && lv2.method_7909() instanceof class_1781 ? lv2 : class_1799.field_8037;
		}
	}

	private class_3414 method_7773(int i) {
		switch (i) {
			case 1:
				return class_3417.field_15011;
			case 2:
				return class_3417.field_14916;
			case 3:
				return class_3417.field_15089;
			default:
				return class_3417.field_14765;
		}
	}

	public static float method_7770(int i, class_1799 arg) {
		float f = (float)i / (float)method_7775(arg);
		if (f > 1.0F) {
			f = 1.0F;
		}

		return f;
	}
}
