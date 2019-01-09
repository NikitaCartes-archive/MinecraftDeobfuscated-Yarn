package net.minecraft;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class class_2614 extends class_2621 implements class_2615, class_3000 {
	private class_2371<class_1799> field_12024 = class_2371.method_10213(5, class_1799.field_8037);
	private int field_12023 = -1;
	private long field_12022;

	public class_2614() {
		super(class_2591.field_11888);
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.field_12024 = class_2371.method_10213(this.method_5439(), class_1799.field_8037);
		if (!this.method_11283(arg)) {
			class_1262.method_5429(arg, this.field_12024);
		}

		this.field_12023 = arg.method_10550("TransferCooldown");
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		if (!this.method_11286(arg)) {
			class_1262.method_5426(arg, this.field_12024);
		}

		arg.method_10569("TransferCooldown", this.field_12023);
		return arg;
	}

	@Override
	public int method_5439() {
		return this.field_12024.size();
	}

	@Override
	public class_1799 method_5434(int i, int j) {
		this.method_11289(null);
		return class_1262.method_5430(this.method_11282(), i, j);
	}

	@Override
	public void method_5447(int i, class_1799 arg) {
		this.method_11289(null);
		this.method_11282().set(i, arg);
		if (arg.method_7947() > this.method_5444()) {
			arg.method_7939(this.method_5444());
		}
	}

	@Override
	protected class_2561 method_5477() {
		return new class_2588("container.hopper");
	}

	@Override
	public void method_16896() {
		if (this.field_11863 != null && !this.field_11863.field_9236) {
			this.field_12023--;
			this.field_12022 = this.field_11863.method_8510();
			if (!this.method_11239()) {
				this.method_11238(0);
				this.method_11243(() -> method_11241(this));
			}
		}
	}

	private boolean method_11243(Supplier<Boolean> supplier) {
		if (this.field_11863 != null && !this.field_11863.field_9236) {
			if (!this.method_11239() && (Boolean)this.method_11010().method_11654(class_2377.field_11126)) {
				boolean bl = false;
				if (!this.method_11259()) {
					bl = this.method_11246();
				}

				if (!this.method_11256()) {
					bl |= supplier.get();
				}

				if (bl) {
					this.method_11238(8);
					this.method_5431();
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	private boolean method_11259() {
		for (class_1799 lv : this.field_12024) {
			if (!lv.method_7960()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean method_5442() {
		return this.method_11259();
	}

	private boolean method_11256() {
		for (class_1799 lv : this.field_12024) {
			if (lv.method_7960() || lv.method_7947() != lv.method_7914()) {
				return false;
			}
		}

		return true;
	}

	private boolean method_11246() {
		class_1263 lv = this.method_11255();
		if (lv == null) {
			return false;
		} else {
			class_2350 lv2 = ((class_2350)this.method_11010().method_11654(class_2377.field_11129)).method_10153();
			if (this.method_11258(lv, lv2)) {
				return false;
			} else {
				for (int i = 0; i < this.method_5439(); i++) {
					if (!this.method_5438(i).method_7960()) {
						class_1799 lv3 = this.method_5438(i).method_7972();
						class_1799 lv4 = method_11260(this, lv, this.method_5434(i, 1), lv2);
						if (lv4.method_7960()) {
							lv.method_5431();
							return true;
						}

						this.method_5447(i, lv3);
					}
				}

				return false;
			}
		}
	}

	private boolean method_11258(class_1263 arg, class_2350 arg2) {
		if (arg instanceof class_1278) {
			class_1278 lv = (class_1278)arg;
			int[] is = lv.method_5494(arg2);

			for (int i : is) {
				class_1799 lv2 = lv.method_5438(i);
				if (lv2.method_7960() || lv2.method_7947() != lv2.method_7914()) {
					return false;
				}
			}
		} else {
			int j = arg.method_5439();

			for (int k = 0; k < j; k++) {
				class_1799 lv3 = arg.method_5438(k);
				if (lv3.method_7960() || lv3.method_7947() != lv3.method_7914()) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean method_11257(class_1263 arg, class_2350 arg2) {
		if (arg instanceof class_1278) {
			class_1278 lv = (class_1278)arg;
			int[] is = lv.method_5494(arg2);

			for (int i : is) {
				if (!lv.method_5438(i).method_7960()) {
					return false;
				}
			}
		} else {
			int j = arg.method_5439();

			for (int k = 0; k < j; k++) {
				if (!arg.method_5438(k).method_7960()) {
					return false;
				}
			}
		}

		return true;
	}

	public static boolean method_11241(class_2615 arg) {
		class_1263 lv = method_11248(arg);
		if (lv != null) {
			class_2350 lv2 = class_2350.field_11033;
			if (method_11257(lv, lv2)) {
				return false;
			}

			if (lv instanceof class_1278) {
				class_1278 lv3 = (class_1278)lv;
				int[] is = lv3.method_5494(lv2);

				for (int i : is) {
					if (method_11261(arg, lv, i, lv2)) {
						return true;
					}
				}
			} else {
				int j = lv.method_5439();

				for (int k = 0; k < j; k++) {
					if (method_11261(arg, lv, k, lv2)) {
						return true;
					}
				}
			}
		} else {
			for (class_1542 lv4 : method_11237(arg)) {
				if (method_11247(arg, lv4)) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean method_11261(class_2615 arg, class_1263 arg2, int i, class_2350 arg3) {
		class_1799 lv = arg2.method_5438(i);
		if (!lv.method_7960() && method_11252(arg2, lv, i, arg3)) {
			class_1799 lv2 = lv.method_7972();
			class_1799 lv3 = method_11260(arg2, arg, arg2.method_5434(i, 1), null);
			if (lv3.method_7960()) {
				arg2.method_5431();
				return true;
			}

			arg2.method_5447(i, lv2);
		}

		return false;
	}

	public static boolean method_11247(class_1263 arg, class_1542 arg2) {
		boolean bl = false;
		class_1799 lv = arg2.method_6983().method_7972();
		class_1799 lv2 = method_11260(null, arg, lv, null);
		if (lv2.method_7960()) {
			bl = true;
			arg2.method_5650();
		} else {
			arg2.method_6979(lv2);
		}

		return bl;
	}

	public static class_1799 method_11260(@Nullable class_1263 arg, class_1263 arg2, class_1799 arg3, @Nullable class_2350 arg4) {
		if (arg2 instanceof class_1278 && arg4 != null) {
			class_1278 lv = (class_1278)arg2;
			int[] is = lv.method_5494(arg4);

			for (int i = 0; i < is.length && !arg3.method_7960(); i++) {
				arg3 = method_11253(arg, arg2, arg3, is[i], arg4);
			}
		} else {
			int j = arg2.method_5439();

			for (int k = 0; k < j && !arg3.method_7960(); k++) {
				arg3 = method_11253(arg, arg2, arg3, k, arg4);
			}
		}

		return arg3;
	}

	private static boolean method_11244(class_1263 arg, class_1799 arg2, int i, @Nullable class_2350 arg3) {
		return !arg.method_5437(i, arg2) ? false : !(arg instanceof class_1278) || ((class_1278)arg).method_5492(i, arg2, arg3);
	}

	private static boolean method_11252(class_1263 arg, class_1799 arg2, int i, class_2350 arg3) {
		return !(arg instanceof class_1278) || ((class_1278)arg).method_5493(i, arg2, arg3);
	}

	private static class_1799 method_11253(@Nullable class_1263 arg, class_1263 arg2, class_1799 arg3, int i, @Nullable class_2350 arg4) {
		class_1799 lv = arg2.method_5438(i);
		if (method_11244(arg2, arg3, i, arg4)) {
			boolean bl = false;
			boolean bl2 = arg2.method_5442();
			if (lv.method_7960()) {
				arg2.method_5447(i, arg3);
				arg3 = class_1799.field_8037;
				bl = true;
			} else if (method_11254(lv, arg3)) {
				int j = arg3.method_7914() - lv.method_7947();
				int k = Math.min(arg3.method_7947(), j);
				arg3.method_7934(k);
				lv.method_7933(k);
				bl = k > 0;
			}

			if (bl) {
				if (bl2 && arg2 instanceof class_2614) {
					class_2614 lv2 = (class_2614)arg2;
					if (!lv2.method_11242()) {
						int k = 0;
						if (arg instanceof class_2614) {
							class_2614 lv3 = (class_2614)arg;
							if (lv2.field_12022 >= lv3.field_12022) {
								k = 1;
							}
						}

						lv2.method_11238(8 - k);
					}
				}

				arg2.method_5431();
			}
		}

		return arg3;
	}

	@Nullable
	private class_1263 method_11255() {
		class_2350 lv = this.method_11010().method_11654(class_2377.field_11129);
		return method_11250(this.method_10997(), this.field_11867.method_10093(lv));
	}

	@Nullable
	public static class_1263 method_11248(class_2615 arg) {
		return method_11251(arg.method_10997(), arg.method_11266(), arg.method_11264() + 1.0, arg.method_11265());
	}

	public static List<class_1542> method_11237(class_2615 arg) {
		return (List<class_1542>)arg.method_11262()
			.method_1090()
			.stream()
			.flatMap(
				arg2 -> arg.method_10997()
						.method_8390(class_1542.class, arg2.method_989(arg.method_11266() - 0.5, arg.method_11264() - 0.5, arg.method_11265() - 0.5), class_1301.field_6154)
						.stream()
			)
			.collect(Collectors.toList());
	}

	@Nullable
	public static class_1263 method_11250(class_1937 arg, class_2338 arg2) {
		return method_11251(arg, (double)arg2.method_10263() + 0.5, (double)arg2.method_10264() + 0.5, (double)arg2.method_10260() + 0.5);
	}

	@Nullable
	public static class_1263 method_11251(class_1937 arg, double d, double e, double f) {
		class_1263 lv = null;
		class_2338 lv2 = new class_2338(d, e, f);
		class_2680 lv3 = arg.method_8320(lv2);
		class_2248 lv4 = lv3.method_11614();
		if (lv4.method_9570()) {
			class_2586 lv5 = arg.method_8321(lv2);
			if (lv5 instanceof class_1263) {
				lv = (class_1263)lv5;
				if (lv instanceof class_2595 && lv4 instanceof class_2281) {
					lv = class_2281.method_17458(lv3, arg, lv2, true);
				}
			}
		}

		if (lv == null) {
			List<class_1297> list = arg.method_8333(null, new class_238(d - 0.5, e - 0.5, f - 0.5, d + 0.5, e + 0.5, f + 0.5), class_1301.field_6152);
			if (!list.isEmpty()) {
				lv = (class_1263)list.get(arg.field_9229.nextInt(list.size()));
			}
		}

		return lv;
	}

	private static boolean method_11254(class_1799 arg, class_1799 arg2) {
		if (arg.method_7909() != arg2.method_7909()) {
			return false;
		} else if (arg.method_7919() != arg2.method_7919()) {
			return false;
		} else {
			return arg.method_7947() > arg.method_7914() ? false : class_1799.method_7975(arg, arg2);
		}
	}

	@Override
	public double method_11266() {
		return (double)this.field_11867.method_10263() + 0.5;
	}

	@Override
	public double method_11264() {
		return (double)this.field_11867.method_10264() + 0.5;
	}

	@Override
	public double method_11265() {
		return (double)this.field_11867.method_10260() + 0.5;
	}

	private void method_11238(int i) {
		this.field_12023 = i;
	}

	private boolean method_11239() {
		return this.field_12023 > 0;
	}

	private boolean method_11242() {
		return this.field_12023 > 8;
	}

	@Override
	protected class_2371<class_1799> method_11282() {
		return this.field_12024;
	}

	@Override
	protected void method_11281(class_2371<class_1799> arg) {
		this.field_12024 = arg;
	}

	public void method_11236(class_1297 arg) {
		if (arg instanceof class_1542) {
			class_2338 lv = this.method_11016();
			if (class_259.method_1074(
				class_259.method_1078(arg.method_5829().method_989((double)(-lv.method_10263()), (double)(-lv.method_10264()), (double)(-lv.method_10260()))),
				this.method_11262(),
				class_247.field_16896
			)) {
				this.method_11243(() -> method_11247(this, (class_1542)arg));
			}
		}
	}

	@Override
	protected class_1703 method_5465(int i, class_1661 arg) {
		return new class_1722(i, arg, this);
	}
}
