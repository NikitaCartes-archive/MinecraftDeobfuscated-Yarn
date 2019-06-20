package net.minecraft;

import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public abstract class class_2794<C extends class_2888> {
	protected final class_1936 field_12760;
	protected final long field_12759;
	protected final class_1966 field_12761;
	protected final C field_16567;

	public class_2794(class_1936 arg, class_1966 arg2, C arg3) {
		this.field_12760 = arg;
		this.field_12759 = arg.method_8412();
		this.field_12761 = arg2;
		this.field_16567 = arg3;
	}

	public void method_12106(class_2791 arg) {
		class_1923 lv = arg.method_12004();
		int i = lv.field_9181;
		int j = lv.field_9180;
		class_1959[] lvs = this.field_12761.method_8756(i * 16, j * 16, 16, 16);
		arg.method_12022(lvs);
	}

	protected class_1959 method_16553(class_2791 arg) {
		return arg.method_16552(class_2338.field_10980);
	}

	protected class_1959 method_16554(class_3233 arg, class_2338 arg2) {
		return this.field_12761.method_8758(arg2);
	}

	public void method_12108(class_2791 arg, class_2893.class_2894 arg2) {
		class_2919 lv = new class_2919();
		int i = 8;
		class_1923 lv2 = arg.method_12004();
		int j = lv2.field_9181;
		int k = lv2.field_9180;
		BitSet bitSet = arg.method_12025(arg2);

		for (int l = j - 8; l <= j + 8; l++) {
			for (int m = k - 8; m <= k + 8; m++) {
				List<class_2922<?>> list = this.method_16553(arg).method_8717(arg2);
				ListIterator<class_2922<?>> listIterator = list.listIterator();

				while (listIterator.hasNext()) {
					int n = listIterator.nextIndex();
					class_2922<?> lv3 = (class_2922<?>)listIterator.next();
					lv.method_12663(this.field_12759 + (long)n, l, m);
					if (lv3.method_12669(lv, l, m)) {
						lv3.method_12668(arg, lv, this.method_16398(), l, m, j, k, bitSet);
					}
				}
			}
		}
	}

	@Nullable
	public class_2338 method_12103(class_1937 arg, String string, class_2338 arg2, int i, boolean bl) {
		class_3195<?> lv = (class_3195<?>)class_3031.field_13557.get(string.toLowerCase(Locale.ROOT));
		return lv != null ? lv.method_14015(arg, this, arg2, i, bl) : null;
	}

	public void method_12102(class_3233 arg) {
		int i = arg.method_14336();
		int j = arg.method_14339();
		int k = i * 16;
		int l = j * 16;
		class_2338 lv = new class_2338(k, 0, l);
		class_1959 lv2 = this.method_16554(arg, lv.method_10069(8, 8, 8));
		class_2919 lv3 = new class_2919();
		long m = lv3.method_12661(arg.method_8412(), k, l);

		for (class_2893.class_2895 lv4 : class_2893.class_2895.values()) {
			lv2.method_8702(lv4, this, arg, m, lv3, lv);
		}
	}

	public abstract void method_12110(class_2791 arg);

	public void method_12107(class_3233 arg) {
	}

	public C method_12109() {
		return this.field_16567;
	}

	public abstract int method_12100();

	public void method_12099(class_3218 arg, boolean bl, boolean bl2) {
	}

	public boolean method_12097(class_1959 arg, class_3195<? extends class_3037> arg2) {
		return arg.method_8684(arg2);
	}

	@Nullable
	public <C extends class_3037> C method_12105(class_1959 arg, class_3195<C> arg2) {
		return arg.method_8706(arg2);
	}

	public class_1966 method_12098() {
		return this.field_12761;
	}

	public long method_12101() {
		return this.field_12759;
	}

	public int method_12104() {
		return 256;
	}

	public List<class_1959.class_1964> method_12113(class_1311 arg, class_2338 arg2) {
		return this.field_12760.method_8310(arg2).method_8700(arg);
	}

	public void method_16129(class_2791 arg, class_2794<?> arg2, class_3485 arg3) {
		for (class_3195<?> lv : class_3031.field_13557.values()) {
			if (arg2.method_12098().method_8754(lv)) {
				class_2919 lv2 = new class_2919();
				class_1923 lv3 = arg.method_12004();
				class_3449 lv4 = class_3449.field_16713;
				if (lv.method_14026(arg2, lv2, lv3.field_9181, lv3.field_9180)) {
					class_1959 lv5 = this.method_12098().method_8758(new class_2338(lv3.method_8326() + 9, 0, lv3.method_8328() + 9));
					class_3449 lv6 = lv.method_14016().create(lv, lv3.field_9181, lv3.field_9180, lv5, class_3341.method_14665(), 0, arg2.method_12101());
					lv6.method_16655(this, arg3, lv3.field_9181, lv3.field_9180, lv5);
					lv4 = lv6.method_16657() ? lv6 : class_3449.field_16713;
				}

				arg.method_12184(lv.method_14019(), lv4);
			}
		}
	}

	public void method_16130(class_1936 arg, class_2791 arg2) {
		int i = 8;
		int j = arg2.method_12004().field_9181;
		int k = arg2.method_12004().field_9180;
		int l = j << 4;
		int m = k << 4;

		for (int n = j - 8; n <= j + 8; n++) {
			for (int o = k - 8; o <= k + 8; o++) {
				long p = class_1923.method_8331(n, o);

				for (Entry<String, class_3449> entry : arg.method_8392(n, o).method_12016().entrySet()) {
					class_3449 lv = (class_3449)entry.getValue();
					if (lv != class_3449.field_16713 && lv.method_14968().method_14669(l, m, l + 15, m + 15)) {
						arg2.method_12182((String)entry.getKey(), p);
						class_4209.method_19474(arg, lv);
					}
				}
			}
		}
	}

	public abstract void method_12088(class_1936 arg, class_2791 arg2);

	public int method_16398() {
		return 63;
	}

	public abstract int method_16397(int i, int j, class_2902.class_2903 arg);

	public int method_20402(int i, int j, class_2902.class_2903 arg) {
		return this.method_16397(i, j, arg);
	}

	public int method_18028(int i, int j, class_2902.class_2903 arg) {
		return this.method_16397(i, j, arg) - 1;
	}
}
