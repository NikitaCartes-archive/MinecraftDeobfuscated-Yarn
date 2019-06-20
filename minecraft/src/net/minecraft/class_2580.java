package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2580 extends class_2586 implements class_3908, class_3000 {
	public static final class_1291[][] field_11801 = new class_1291[][]{
		{class_1294.field_5904, class_1294.field_5917}, {class_1294.field_5907, class_1294.field_5913}, {class_1294.field_5910}, {class_1294.field_5924}
	};
	private static final Set<class_1291> field_11798 = (Set<class_1291>)Arrays.stream(field_11801).flatMap(Arrays::stream).collect(Collectors.toSet());
	private List<class_2580.class_2581> field_19177 = Lists.<class_2580.class_2581>newArrayList();
	private List<class_2580.class_2581> field_19178 = Lists.<class_2580.class_2581>newArrayList();
	private int field_11803 = 0;
	private int field_19179 = -1;
	@Nullable
	private class_1291 field_11795;
	@Nullable
	private class_1291 field_11799;
	@Nullable
	private class_2561 field_11793;
	private class_1273 field_17377 = class_1273.field_5817;
	private final class_3913 field_17378 = new class_3913() {
		@Override
		public int method_17390(int i) {
			switch (i) {
				case 0:
					return class_2580.this.field_11803;
				case 1:
					return class_1291.method_5554(class_2580.this.field_11795);
				case 2:
					return class_1291.method_5554(class_2580.this.field_11799);
				default:
					return 0;
			}
		}

		@Override
		public void method_17391(int i, int j) {
			switch (i) {
				case 0:
					class_2580.this.field_11803 = j;
					break;
				case 1:
					if (!class_2580.this.field_11863.field_9236 && !class_2580.this.field_19177.isEmpty()) {
						class_2580.this.method_10938(class_3417.field_14891);
					}

					class_2580.this.field_11795 = class_2580.method_10934(j);
					break;
				case 2:
					class_2580.this.field_11799 = class_2580.method_10934(j);
			}
		}

		@Override
		public int method_17389() {
			return 3;
		}
	};

	public class_2580() {
		super(class_2591.field_11890);
	}

	@Override
	public void method_16896() {
		int i = this.field_11867.method_10263();
		int j = this.field_11867.method_10264();
		int k = this.field_11867.method_10260();
		class_2338 lv;
		if (this.field_19179 < j) {
			lv = this.field_11867;
			this.field_19178 = Lists.<class_2580.class_2581>newArrayList();
			this.field_19179 = lv.method_10264() - 1;
		} else {
			lv = new class_2338(i, this.field_19179 + 1, k);
		}

		class_2580.class_2581 lv2 = this.field_19178.isEmpty() ? null : (class_2580.class_2581)this.field_19178.get(this.field_19178.size() - 1);
		int l = this.field_11863.method_8589(class_2902.class_2903.field_13202, i, k);

		for (int m = 0; m < 10 && lv.method_10264() <= l; m++) {
			class_2680 lv3 = this.field_11863.method_8320(lv);
			class_2248 lv4 = lv3.method_11614();
			if (lv4 instanceof class_4275) {
				float[] fs = ((class_4275)lv4).method_10622().method_7787();
				if (this.field_19178.size() <= 1) {
					lv2 = new class_2580.class_2581(fs);
					this.field_19178.add(lv2);
				} else if (lv2 != null) {
					if (Arrays.equals(fs, lv2.field_11805)) {
						lv2.method_10942();
					} else {
						lv2 = new class_2580.class_2581(
							new float[]{(lv2.field_11805[0] + fs[0]) / 2.0F, (lv2.field_11805[1] + fs[1]) / 2.0F, (lv2.field_11805[2] + fs[2]) / 2.0F}
						);
						this.field_19178.add(lv2);
					}
				}
			} else {
				if (lv2 == null || lv3.method_11581(this.field_11863, lv) >= 15 && lv4 != class_2246.field_9987) {
					this.field_19178.clear();
					this.field_19179 = l;
					break;
				}

				lv2.method_10942();
			}

			lv = lv.method_10084();
			this.field_19179++;
		}

		int m = this.field_11803;
		if (this.field_11863.method_8510() % 80L == 0L) {
			if (!this.field_19177.isEmpty()) {
				this.method_20293(i, j, k);
			}

			if (this.field_11803 > 0 && !this.field_19177.isEmpty()) {
				this.method_10940();
				this.method_10938(class_3417.field_15045);
			}
		}

		if (this.field_19179 >= l) {
			this.field_19179 = -1;
			boolean bl = m > 0;
			this.field_19177 = this.field_19178;
			if (!this.field_11863.field_9236) {
				boolean bl2 = this.field_11803 > 0;
				if (!bl && bl2) {
					this.method_10938(class_3417.field_14703);

					for (class_3222 lv5 : this.field_11863
						.method_18467(class_3222.class, new class_238((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k).method_1009(10.0, 5.0, 10.0))) {
						class_174.field_1189.method_8812(lv5, this);
					}
				}
			}
		}
	}

	private void method_20293(int i, int j, int k) {
		this.field_11803 = 0;

		for (int l = 1; l <= 4; this.field_11803 = l++) {
			int m = j - l;
			if (m < 0) {
				break;
			}

			boolean bl = true;

			for (int n = i - l; n <= i + l && bl; n++) {
				for (int o = k - l; o <= k + l; o++) {
					class_2248 lv = this.field_11863.method_8320(new class_2338(n, m, o)).method_11614();
					if (lv != class_2246.field_10234 && lv != class_2246.field_10205 && lv != class_2246.field_10201 && lv != class_2246.field_10085) {
						bl = false;
						break;
					}
				}
			}

			if (!bl) {
				break;
			}
		}
	}

	@Override
	public void method_11012() {
		this.method_10938(class_3417.field_19344);
		super.method_11012();
	}

	private void method_10940() {
		if (!this.field_11863.field_9236 && this.field_11795 != null) {
			double d = (double)(this.field_11803 * 10 + 10);
			int i = 0;
			if (this.field_11803 >= 4 && this.field_11795 == this.field_11799) {
				i = 1;
			}

			int j = (9 + this.field_11803 * 2) * 20;
			class_238 lv = new class_238(this.field_11867).method_1014(d).method_1012(0.0, (double)this.field_11863.method_8322(), 0.0);
			List<class_1657> list = this.field_11863.method_18467(class_1657.class, lv);

			for (class_1657 lv2 : list) {
				lv2.method_6092(new class_1293(this.field_11795, j, i, true, true));
			}

			if (this.field_11803 >= 4 && this.field_11795 != this.field_11799 && this.field_11799 != null) {
				for (class_1657 lv2 : list) {
					lv2.method_6092(new class_1293(this.field_11799, j, 0, true, true));
				}
			}
		}
	}

	public void method_10938(class_3414 arg) {
		this.field_11863.method_8396(null, this.field_11867, arg, class_3419.field_15245, 1.0F, 1.0F);
	}

	@Environment(EnvType.CLIENT)
	public List<class_2580.class_2581> method_10937() {
		return (List<class_2580.class_2581>)(this.field_11803 == 0 ? ImmutableList.of() : this.field_19177);
	}

	public int method_10939() {
		return this.field_11803;
	}

	@Nullable
	@Override
	public class_2622 method_16886() {
		return new class_2622(this.field_11867, 3, this.method_16887());
	}

	@Override
	public class_2487 method_16887() {
		return this.method_11007(new class_2487());
	}

	@Environment(EnvType.CLIENT)
	@Override
	public double method_11006() {
		return 65536.0;
	}

	@Nullable
	private static class_1291 method_10934(int i) {
		class_1291 lv = class_1291.method_5569(i);
		return field_11798.contains(lv) ? lv : null;
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.field_11795 = method_10934(arg.method_10550("Primary"));
		this.field_11799 = method_10934(arg.method_10550("Secondary"));
		if (arg.method_10573("CustomName", 8)) {
			this.field_11793 = class_2561.class_2562.method_10877(arg.method_10558("CustomName"));
		}

		this.field_17377 = class_1273.method_5473(arg);
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		arg.method_10569("Primary", class_1291.method_5554(this.field_11795));
		arg.method_10569("Secondary", class_1291.method_5554(this.field_11799));
		arg.method_10569("Levels", this.field_11803);
		if (this.field_11793 != null) {
			arg.method_10582("CustomName", class_2561.class_2562.method_10867(this.field_11793));
		}

		this.field_17377.method_5474(arg);
		return arg;
	}

	public void method_10936(@Nullable class_2561 arg) {
		this.field_11793 = arg;
	}

	@Nullable
	@Override
	public class_1703 createMenu(int i, class_1661 arg, class_1657 arg2) {
		return class_2624.method_17487(arg2, this.field_17377, this.method_5476())
			? new class_1704(i, arg, this.field_17378, class_3914.method_17392(this.field_11863, this.method_11016()))
			: null;
	}

	@Override
	public class_2561 method_5476() {
		return (class_2561)(this.field_11793 != null ? this.field_11793 : new class_2588("container.beacon"));
	}

	public static class class_2581 {
		private final float[] field_11805;
		private int field_11804;

		public class_2581(float[] fs) {
			this.field_11805 = fs;
			this.field_11804 = 1;
		}

		protected void method_10942() {
			this.field_11804++;
		}

		@Environment(EnvType.CLIENT)
		public float[] method_10944() {
			return this.field_11805;
		}

		@Environment(EnvType.CLIENT)
		public int method_10943() {
			return this.field_11804;
		}
	}
}
