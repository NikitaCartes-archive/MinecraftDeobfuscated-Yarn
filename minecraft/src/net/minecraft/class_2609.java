package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public abstract class class_2609 extends class_2624 implements class_1278, class_1732, class_1737, class_3000 {
	private static final int[] field_11987 = new int[]{0};
	private static final int[] field_11982 = new int[]{2, 1};
	private static final int[] field_11983 = new int[]{1};
	protected class_2371<class_1799> field_11984 = class_2371.method_10213(3, class_1799.field_8037);
	private int field_11981;
	private int field_11980;
	private int field_11989;
	private int field_11988;
	protected final class_3913 field_17374 = new class_3913() {
		@Override
		public int method_17390(int i) {
			switch (i) {
				case 0:
					return class_2609.this.field_11981;
				case 1:
					return class_2609.this.field_11980;
				case 2:
					return class_2609.this.field_11989;
				case 3:
					return class_2609.this.field_11988;
				default:
					return 0;
			}
		}

		@Override
		public void method_17391(int i, int j) {
			switch (i) {
				case 0:
					class_2609.this.field_11981 = j;
					break;
				case 1:
					class_2609.this.field_11980 = j;
					break;
				case 2:
					class_2609.this.field_11989 = j;
					break;
				case 3:
					class_2609.this.field_11988 = j;
			}
		}

		@Override
		public int method_17389() {
			return 4;
		}
	};
	private final Map<class_2960, Integer> field_11986 = Maps.<class_2960, Integer>newHashMap();
	protected final class_3956<? extends class_1874> field_17582;

	protected class_2609(class_2591<?> arg, class_3956<? extends class_1874> arg2) {
		super(arg);
		this.field_17582 = arg2;
	}

	public static Map<class_1792, Integer> method_11196() {
		Map<class_1792, Integer> map = Maps.<class_1792, Integer>newLinkedHashMap();
		method_11202(map, class_1802.field_8187, 20000);
		method_11202(map, class_2246.field_10381, 16000);
		method_11202(map, class_1802.field_8894, 2400);
		method_11202(map, class_1802.field_8713, 1600);
		method_11202(map, class_1802.field_8665, 1600);
		method_11194(map, class_3489.field_15539, 300);
		method_11194(map, class_3489.field_15537, 300);
		method_11194(map, class_3489.field_15557, 300);
		method_11194(map, class_3489.field_15534, 150);
		method_11194(map, class_3489.field_15550, 300);
		method_11194(map, class_3489.field_15540, 300);
		method_11202(map, class_2246.field_10620, 300);
		method_11202(map, class_2246.field_10299, 300);
		method_11202(map, class_2246.field_10020, 300);
		method_11202(map, class_2246.field_10319, 300);
		method_11202(map, class_2246.field_10132, 300);
		method_11202(map, class_2246.field_10144, 300);
		method_11202(map, class_2246.field_10188, 300);
		method_11202(map, class_2246.field_10513, 300);
		method_11202(map, class_2246.field_10291, 300);
		method_11202(map, class_2246.field_10041, 300);
		method_11202(map, class_2246.field_10196, 300);
		method_11202(map, class_2246.field_10457, 300);
		method_11202(map, class_2246.field_10179, 300);
		method_11202(map, class_2246.field_10504, 300);
		method_11202(map, class_2246.field_16330, 300);
		method_11202(map, class_2246.field_10223, 300);
		method_11202(map, class_2246.field_10034, 300);
		method_11202(map, class_2246.field_10380, 300);
		method_11202(map, class_2246.field_9980, 300);
		method_11202(map, class_2246.field_10429, 300);
		method_11194(map, class_3489.field_15556, 300);
		method_11202(map, class_1802.field_8102, 300);
		method_11202(map, class_1802.field_8378, 300);
		method_11202(map, class_2246.field_9983, 300);
		method_11194(map, class_3489.field_15533, 200);
		method_11202(map, class_1802.field_8876, 200);
		method_11202(map, class_1802.field_8091, 200);
		method_11202(map, class_1802.field_8167, 200);
		method_11202(map, class_1802.field_8406, 200);
		method_11202(map, class_1802.field_8647, 200);
		method_11194(map, class_3489.field_15552, 200);
		method_11194(map, class_3489.field_15536, 200);
		method_11194(map, class_3489.field_15544, 100);
		method_11194(map, class_3489.field_15555, 100);
		method_11202(map, class_1802.field_8600, 100);
		method_11194(map, class_3489.field_15528, 100);
		method_11202(map, class_1802.field_8428, 100);
		method_11194(map, class_3489.field_15542, 67);
		method_11202(map, class_2246.field_10342, 4001);
		method_11202(map, class_1802.field_8399, 300);
		method_11202(map, class_2246.field_10211, 50);
		method_11202(map, class_2246.field_10428, 100);
		method_11202(map, class_2246.field_16492, 50);
		method_11202(map, class_2246.field_10083, 300);
		method_11202(map, class_2246.field_16328, 300);
		method_11202(map, class_2246.field_16336, 300);
		method_11202(map, class_2246.field_16331, 300);
		method_11202(map, class_2246.field_16329, 300);
		method_11202(map, class_2246.field_17563, 300);
		return map;
	}

	private static void method_11194(Map<class_1792, Integer> map, class_3494<class_1792> arg, int i) {
		for (class_1792 lv : arg.method_15138()) {
			map.put(lv, i);
		}
	}

	private static void method_11202(Map<class_1792, Integer> map, class_1935 arg, int i) {
		map.put(arg.method_8389(), i);
	}

	private boolean method_11201() {
		return this.field_11981 > 0;
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.field_11984 = class_2371.method_10213(this.method_5439(), class_1799.field_8037);
		class_1262.method_5429(arg, this.field_11984);
		this.field_11981 = arg.method_10568("BurnTime");
		this.field_11989 = arg.method_10568("CookTime");
		this.field_11988 = arg.method_10568("CookTimeTotal");
		this.field_11980 = this.method_11200(this.field_11984.get(1));
		int i = arg.method_10568("RecipesUsedSize");

		for (int j = 0; j < i; j++) {
			class_2960 lv = new class_2960(arg.method_10558("RecipeLocation" + j));
			int k = arg.method_10550("RecipeAmount" + j);
			this.field_11986.put(lv, k);
		}
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		arg.method_10575("BurnTime", (short)this.field_11981);
		arg.method_10575("CookTime", (short)this.field_11989);
		arg.method_10575("CookTimeTotal", (short)this.field_11988);
		class_1262.method_5426(arg, this.field_11984);
		arg.method_10575("RecipesUsedSize", (short)this.field_11986.size());
		int i = 0;

		for (Entry<class_2960, Integer> entry : this.field_11986.entrySet()) {
			arg.method_10582("RecipeLocation" + i, ((class_2960)entry.getKey()).toString());
			arg.method_10569("RecipeAmount" + i, (Integer)entry.getValue());
			i++;
		}

		return arg;
	}

	@Override
	public void method_16896() {
		boolean bl = this.method_11201();
		boolean bl2 = false;
		if (this.method_11201()) {
			this.field_11981--;
		}

		if (!this.field_11863.field_9236) {
			class_1799 lv = this.field_11984.get(1);
			if (this.method_11201() || !lv.method_7960() && !this.field_11984.get(0).method_7960()) {
				class_1860<?> lv2 = (class_1860<?>)this.field_11863.method_8433().method_8132(this.field_17582, this, this.field_11863).orElse(null);
				if (!this.method_11201() && this.method_11192(lv2)) {
					this.field_11981 = this.method_11200(lv);
					this.field_11980 = this.field_11981;
					if (this.method_11201()) {
						bl2 = true;
						if (!lv.method_7960()) {
							class_1792 lv3 = lv.method_7909();
							lv.method_7934(1);
							if (lv.method_7960()) {
								class_1792 lv4 = lv3.method_7858();
								this.field_11984.set(1, lv4 == null ? class_1799.field_8037 : new class_1799(lv4));
							}
						}
					}
				}

				if (this.method_11201() && this.method_11192(lv2)) {
					this.field_11989++;
					if (this.field_11989 == this.field_11988) {
						this.field_11989 = 0;
						this.field_11988 = this.method_17029();
						this.method_11203(lv2);
						bl2 = true;
					}
				} else {
					this.field_11989 = 0;
				}
			} else if (!this.method_11201() && this.field_11989 > 0) {
				this.field_11989 = class_3532.method_15340(this.field_11989 - 2, 0, this.field_11988);
			}

			if (bl != this.method_11201()) {
				bl2 = true;
				this.field_11863
					.method_8652(
						this.field_11867, this.field_11863.method_8320(this.field_11867).method_11657(class_2363.field_11105, Boolean.valueOf(this.method_11201())), 3
					);
			}
		}

		if (bl2) {
			this.method_5431();
		}
	}

	protected boolean method_11192(@Nullable class_1860<?> arg) {
		if (!this.field_11984.get(0).method_7960() && arg != null) {
			class_1799 lv = arg.method_8110();
			if (lv.method_7960()) {
				return false;
			} else {
				class_1799 lv2 = this.field_11984.get(2);
				if (lv2.method_7960()) {
					return true;
				} else if (!lv2.method_7962(lv)) {
					return false;
				} else {
					return lv2.method_7947() < this.method_5444() && lv2.method_7947() < lv2.method_7914() ? true : lv2.method_7947() < lv.method_7914();
				}
			}
		} else {
			return false;
		}
	}

	private void method_11203(@Nullable class_1860<?> arg) {
		if (arg != null && this.method_11192(arg)) {
			class_1799 lv = this.field_11984.get(0);
			class_1799 lv2 = arg.method_8110();
			class_1799 lv3 = this.field_11984.get(2);
			if (lv3.method_7960()) {
				this.field_11984.set(2, lv2.method_7972());
			} else if (lv3.method_7909() == lv2.method_7909()) {
				lv3.method_7933(1);
			}

			if (!this.field_11863.field_9236) {
				this.method_7662(arg);
			}

			if (lv.method_7909() == class_2246.field_10562.method_8389()
				&& !this.field_11984.get(1).method_7960()
				&& this.field_11984.get(1).method_7909() == class_1802.field_8550) {
				this.field_11984.set(1, new class_1799(class_1802.field_8705));
			}

			lv.method_7934(1);
		}
	}

	protected int method_11200(class_1799 arg) {
		if (arg.method_7960()) {
			return 0;
		} else {
			class_1792 lv = arg.method_7909();
			return (Integer)method_11196().getOrDefault(lv, 0);
		}
	}

	protected int method_17029() {
		return (Integer)this.field_11863.method_8433().method_8132(this.field_17582, this, this.field_11863).map(class_1874::method_8167).orElse(200);
	}

	public static boolean method_11195(class_1799 arg) {
		return method_11196().containsKey(arg.method_7909());
	}

	@Override
	public int[] method_5494(class_2350 arg) {
		if (arg == class_2350.field_11033) {
			return field_11982;
		} else {
			return arg == class_2350.field_11036 ? field_11987 : field_11983;
		}
	}

	@Override
	public boolean method_5492(int i, class_1799 arg, @Nullable class_2350 arg2) {
		return this.method_5437(i, arg);
	}

	@Override
	public boolean method_5493(int i, class_1799 arg, class_2350 arg2) {
		if (arg2 == class_2350.field_11033 && i == 1) {
			class_1792 lv = arg.method_7909();
			if (lv != class_1802.field_8705 && lv != class_1802.field_8550) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int method_5439() {
		return this.field_11984.size();
	}

	@Override
	public boolean method_5442() {
		for (class_1799 lv : this.field_11984) {
			if (!lv.method_7960()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public class_1799 method_5438(int i) {
		return this.field_11984.get(i);
	}

	@Override
	public class_1799 method_5434(int i, int j) {
		return class_1262.method_5430(this.field_11984, i, j);
	}

	@Override
	public class_1799 method_5441(int i) {
		return class_1262.method_5428(this.field_11984, i);
	}

	@Override
	public void method_5447(int i, class_1799 arg) {
		class_1799 lv = this.field_11984.get(i);
		boolean bl = !arg.method_7960() && arg.method_7962(lv) && class_1799.method_7975(arg, lv);
		this.field_11984.set(i, arg);
		if (arg.method_7947() > this.method_5444()) {
			arg.method_7939(this.method_5444());
		}

		if (i == 0 && !bl) {
			this.field_11988 = this.method_17029();
			this.field_11989 = 0;
			this.method_5431();
		}
	}

	@Override
	public boolean method_5443(class_1657 arg) {
		return this.field_11863.method_8321(this.field_11867) != this
			? false
			: arg.method_5649(
					(double)this.field_11867.method_10263() + 0.5, (double)this.field_11867.method_10264() + 0.5, (double)this.field_11867.method_10260() + 0.5
				)
				<= 64.0;
	}

	@Override
	public boolean method_5437(int i, class_1799 arg) {
		if (i == 2) {
			return false;
		} else if (i != 1) {
			return true;
		} else {
			class_1799 lv = this.field_11984.get(1);
			return method_11195(arg) || arg.method_7909() == class_1802.field_8550 && lv.method_7909() != class_1802.field_8550;
		}
	}

	@Override
	public void method_5448() {
		this.field_11984.clear();
	}

	@Override
	public void method_7662(@Nullable class_1860<?> arg) {
		if (arg != null) {
			this.field_11986.compute(arg.method_8114(), (argx, integer) -> 1 + (integer == null ? 0 : integer));
		}
	}

	@Nullable
	@Override
	public class_1860<?> method_7663() {
		return null;
	}

	@Override
	public void method_7664(class_1657 arg) {
	}

	public void method_17763(class_1657 arg) {
		if (!this.field_11863.method_8450().method_8355("doLimitedCrafting")) {
			List<class_1860<?>> list = Lists.<class_1860<?>>newArrayList();

			for (Entry<class_2960, Integer> entry : this.field_11986.entrySet()) {
				arg.field_6002.method_8433().method_8130((class_2960)entry.getKey()).ifPresent(arg2 -> {
					list.add(arg2);
					method_17760(arg, (Integer)entry.getValue(), ((class_1874)arg2).method_8171());
				});
			}

			arg.method_7254(list);
		}

		this.field_11986.clear();
	}

	private static void method_17760(class_1657 arg, int i, float f) {
		if (f == 0.0F) {
			i = 0;
		} else if (f < 1.0F) {
			int j = class_3532.method_15375((float)i * f);
			if (j < class_3532.method_15386((float)i * f) && Math.random() < (double)((float)i * f - (float)j)) {
				j++;
			}

			i = j;
		}

		while (i > 0) {
			int j = class_1303.method_5918(i);
			i -= j;
			arg.field_6002.method_8649(new class_1303(arg.field_6002, arg.field_5987, arg.field_6010 + 0.5, arg.field_6035 + 0.5, j));
		}
	}

	@Override
	public void method_7683(class_1662 arg) {
		for (class_1799 lv : this.field_11984) {
			arg.method_7400(lv);
		}
	}
}
