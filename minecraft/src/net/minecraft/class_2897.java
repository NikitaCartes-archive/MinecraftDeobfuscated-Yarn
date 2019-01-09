package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public class class_2897 extends class_2794<class_3232> {
	private final class_1959 field_13183;
	private final class_2910 field_13184 = new class_2910();

	public class_2897(class_1936 arg, class_1966 arg2, class_3232 arg3) {
		super(arg, arg2, arg3);
		this.field_13183 = this.method_12589();
	}

	private class_1959 method_12589() {
		class_1959 lv = this.field_16567.method_14326();
		class_2897.class_2898 lv2 = new class_2897.class_2898(
			lv.method_8692(),
			lv.method_8694(),
			lv.method_8688(),
			lv.method_8695(),
			lv.method_8686(),
			lv.method_8712(),
			lv.method_8715(),
			lv.method_8687(),
			lv.method_8713(),
			lv.method_8725()
		);
		Map<String, Map<String, String>> map = this.field_16567.method_14333();

		for (String string : map.keySet()) {
			class_2975<?>[] lvs = (class_2975<?>[])class_3232.field_14073.get(string);
			if (lvs != null) {
				for (class_2975<?> lv3 : lvs) {
					lv2.method_8719((class_2893.class_2895)class_3232.field_14069.get(lv3), lv3);
					class_2975<?> lv4 = ((class_2986)lv3.field_13375).field_13399;
					if (lv4.field_13376 instanceof class_3195) {
						class_3195<class_3037> lv5 = (class_3195<class_3037>)lv4.field_13376;
						class_3037 lv6 = lv.method_8706(lv5);
						lv2.method_8710(lv5, lv6 != null ? lv6 : (class_3037)class_3232.field_14080.get(lv3));
					}
				}
			}
		}

		boolean bl = (!this.field_16567.method_14320() || lv == class_1972.field_9473) && map.containsKey("decoration");
		if (bl) {
			List<class_2893.class_2895> list = Lists.<class_2893.class_2895>newArrayList();
			list.add(class_2893.class_2895.field_13172);
			list.add(class_2893.class_2895.field_13173);

			for (class_2893.class_2895 lv7 : class_2893.class_2895.values()) {
				if (!list.contains(lv7)) {
					for (class_2975<?> lv4 : lv.method_8721(lv7)) {
						lv2.method_8719(lv7, lv4);
					}
				}
			}
		}

		return lv2;
	}

	@Override
	public void method_12110(class_2791 arg) {
	}

	@Override
	public int method_12100() {
		class_2791 lv = this.field_12760.method_8392(0, 0);
		return lv.method_12005(class_2902.class_2903.field_13197, 8, 8);
	}

	@Override
	protected class_1959 method_16553(class_2791 arg) {
		return this.field_13183;
	}

	@Override
	protected class_1959 method_16554(class_3233 arg, int i, int j) {
		return this.field_13183;
	}

	@Override
	public void method_12088(class_1936 arg, class_2791 arg2) {
		class_2680[] lvs = this.field_16567.method_14312();
		class_2338.class_2339 lv = new class_2338.class_2339();
		class_2902 lv2 = ((class_2839)arg2).method_12032(class_2902.class_2903.field_13195);
		class_2902 lv3 = ((class_2839)arg2).method_12032(class_2902.class_2903.field_13194);

		for (int i = 0; i < lvs.length; i++) {
			class_2680 lv4 = lvs[i];
			if (lv4 != null) {
				for (int j = 0; j < 16; j++) {
					for (int k = 0; k < 16; k++) {
						arg2.method_12010(lv.method_10103(j, i, k), lv4, false);
						lv2.method_12597(j, i, k, lv4);
						lv3.method_12597(j, i, k, lv4);
					}
				}
			}
		}
	}

	@Override
	public int method_16397(int i, int j, class_2902.class_2903 arg) {
		class_2680[] lvs = this.field_16567.method_14312();

		for (int k = lvs.length - 1; k >= 0; k--) {
			class_2680 lv = lvs[k];
			if (lv != null && arg.method_16402().test(lv)) {
				return k + 1;
			}
		}

		return 0;
	}

	@Override
	public void method_12099(class_1937 arg, boolean bl, boolean bl2) {
		this.field_13184.method_12639(arg, bl, bl2);
	}

	@Override
	public boolean method_12097(class_1959 arg, class_3195<? extends class_3037> arg2) {
		return this.field_13183.method_8684(arg2);
	}

	@Nullable
	@Override
	public <C extends class_3037> C method_12105(class_1959 arg, class_3195<C> arg2) {
		return this.field_13183.method_8706(arg2);
	}

	@Nullable
	@Override
	public class_2338 method_12103(class_1937 arg, String string, class_2338 arg2, int i, boolean bl) {
		return !this.field_16567.method_14333().keySet().contains(string.toLowerCase(Locale.ROOT)) ? null : super.method_12103(arg, string, arg2, i, bl);
	}

	class class_2898 extends class_1959 {
		protected class_2898(
			class_3504<?> arg2, class_1959.class_1963 arg3, class_1959.class_1961 arg4, float f, float g, float h, float i, int j, int k, @Nullable String string
		) {
			super(
				new class_1959.class_1960()
					.method_8731(arg2)
					.method_8735(arg3)
					.method_8738(arg4)
					.method_8740(f)
					.method_8743(g)
					.method_8747(h)
					.method_8727(i)
					.method_8733(j)
					.method_8728(k)
					.method_8745(string)
			);
		}
	}
}
