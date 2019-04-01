package net.minecraft;

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
	private final List<class_2580.class_2581> field_11796 = Lists.<class_2580.class_2581>newArrayList();
	@Environment(EnvType.CLIENT)
	private long field_11797;
	@Environment(EnvType.CLIENT)
	private float field_11802;
	private boolean field_11794;
	private boolean field_11792;
	private int field_11803 = -1;
	@Nullable
	private class_1291 field_11795;
	@Nullable
	private class_1291 field_11799;
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
					class_2580.this.field_11795 = class_2580.method_10934(j);
					break;
				case 2:
					class_2580.this.field_11799 = class_2580.method_10934(j);
			}

			if (!class_2580.this.field_11863.field_9236 && i == 1 && class_2580.this.field_11794) {
				class_2580.this.method_10938(class_3417.field_14891);
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
		if (this.field_11863.method_8510() % 80L == 0L) {
			this.method_10941();
			if (this.field_11794) {
				this.method_10938(class_3417.field_15045);
			}
		}

		if (!this.field_11863.field_9236 && this.field_11794 != this.field_11792) {
			this.field_11792 = this.field_11794;
			this.method_10938(this.field_11794 ? class_3417.field_14703 : class_3417.field_15176);
		}
	}

	public void method_10941() {
		if (this.field_11863 != null) {
			this.method_10935();
			this.method_10940();
		}
	}

	public void method_10938(class_3414 arg) {
		this.field_11863.method_8396(null, this.field_11867, arg, class_3419.field_15245, 1.0F, 1.0F);
	}

	private void method_10940() {
		if (this.field_11794 && this.field_11803 > 0 && !this.field_11863.field_9236 && this.field_11795 != null) {
			double d = (double)(this.field_11803 * 10 + 10);
			int i = 0;
			if (this.field_11803 >= 4 && this.field_11795 == this.field_11799) {
				i = 1;
			}

			int j = (9 + this.field_11803 * 2) * 20;
			int k = this.field_11867.method_10263();
			int l = this.field_11867.method_10264();
			int m = this.field_11867.method_10260();
			class_238 lv = new class_238((double)k, (double)l, (double)m, (double)(k + 1), (double)(l + 1), (double)(m + 1))
				.method_1014(d)
				.method_1012(0.0, (double)this.field_11863.method_8322(), 0.0);
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

	private void method_10935() {
		int i = this.field_11867.method_10263();
		int j = this.field_11867.method_10264();
		int k = this.field_11867.method_10260();
		int l = this.field_11803;
		this.field_11803 = 0;
		this.field_11796.clear();
		this.field_11794 = true;
		class_2580.class_2581 lv = new class_2580.class_2581(class_1767.field_7952.method_7787());
		this.field_11796.add(lv);
		boolean bl = true;
		class_2338.class_2339 lv2 = new class_2338.class_2339();

		for (int m = j + 1; m < 256; m++) {
			class_2680 lv3 = this.field_11863.method_8320(lv2.method_10103(i, m, k));
			class_2248 lv4 = lv3.method_11614();
			float[] fs;
			if (lv4 instanceof class_2506) {
				fs = ((class_2506)lv4).method_10624().method_7787();
			} else {
				if (!(lv4 instanceof class_2504)) {
					if (lv3.method_11581(this.field_11863, lv2) >= 15 && lv4 != class_2246.field_9987) {
						this.field_11794 = false;
						this.field_11796.clear();
						break;
					}

					lv.method_10942();
					continue;
				}

				fs = ((class_2504)lv4).method_10622().method_7787();
			}

			if (!bl) {
				fs = new float[]{(lv.method_10944()[0] + fs[0]) / 2.0F, (lv.method_10944()[1] + fs[1]) / 2.0F, (lv.method_10944()[2] + fs[2]) / 2.0F};
			}

			if (Arrays.equals(fs, lv.method_10944())) {
				lv.method_10942();
			} else {
				lv = new class_2580.class_2581(fs);
				this.field_11796.add(lv);
			}

			bl = false;
		}

		if (this.field_11794) {
			for (int m = 1; m <= 4; this.field_11803 = m++) {
				int n = j - m;
				if (n < 0) {
					break;
				}

				boolean bl2 = true;

				for (int o = i - m; o <= i + m && bl2; o++) {
					for (int p = k - m; p <= k + m; p++) {
						class_2248 lv5 = this.field_11863.method_8320(new class_2338(o, n, p)).method_11614();
						if (lv5 != class_2246.field_10234 && lv5 != class_2246.field_10205 && lv5 != class_2246.field_10201 && lv5 != class_2246.field_10085) {
							bl2 = false;
							break;
						}
					}
				}

				if (!bl2) {
					break;
				}
			}

			if (this.field_11803 == 0) {
				this.field_11794 = false;
			}
		}

		if (!this.field_11863.field_9236 && l < this.field_11803) {
			for (class_3222 lv6 : this.field_11863
				.method_18467(class_3222.class, new class_238((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k).method_1009(10.0, 5.0, 10.0))) {
				class_174.field_1189.method_8812(lv6, this);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public List<class_2580.class_2581> method_10937() {
		return this.field_11796;
	}

	@Environment(EnvType.CLIENT)
	public float method_10933() {
		if (!this.field_11794) {
			return 0.0F;
		} else {
			int i = (int)(this.field_11863.method_8510() - this.field_11797);
			this.field_11797 = this.field_11863.method_8510();
			if (i > 1) {
				this.field_11802 -= (float)i / 40.0F;
				if (this.field_11802 < 0.0F) {
					this.field_11802 = 0.0F;
				}
			}

			this.field_11802 += 0.025F;
			if (this.field_11802 > 1.0F) {
				this.field_11802 = 1.0F;
			}

			return this.field_11802;
		}
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
		this.field_11803 = arg.method_10550("Levels");
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

	@Override
	public boolean method_11004(int i, int j) {
		if (i == 1) {
			this.method_10941();
			return true;
		} else {
			return super.method_11004(i, j);
		}
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

		public float[] method_10944() {
			return this.field_11805;
		}

		@Environment(EnvType.CLIENT)
		public int method_10943() {
			return this.field_11804;
		}
	}
}
