package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class class_1418 extends class_18 {
	private class_1937 field_6717;
	private final List<class_2338> field_6718 = Lists.<class_2338>newArrayList();
	private final List<class_1417> field_6714 = Lists.<class_1417>newArrayList();
	private final List<class_1415> field_6715 = Lists.<class_1415>newArrayList();
	private int field_6716;

	public class_1418(String string) {
		super(string);
	}

	public class_1418(class_1937 arg) {
		super(method_6434(arg.field_9247));
		this.field_6717 = arg;
		this.method_80();
	}

	public void method_6433(class_1937 arg) {
		this.field_6717 = arg;

		for (class_1415 lv : this.field_6715) {
			lv.method_6399(arg);
		}
	}

	public void method_6435(class_2338 arg) {
		if (this.field_6718.size() <= 64) {
			if (!this.method_6441(arg)) {
				this.field_6718.add(arg);
			}
		}
	}

	public void method_6440() {
		this.field_6716++;

		for (class_1415 lv : this.field_6715) {
			lv.method_6400(this.field_6716);
			this.field_6717.method_16542().method_16531(lv);
		}

		this.method_6430();
		this.method_6431();
		this.method_6432();
		if (this.field_6716 % 400 == 0) {
			this.method_80();
		}
	}

	private void method_6430() {
		Iterator<class_1415> iterator = this.field_6715.iterator();

		while (iterator.hasNext()) {
			class_1415 lv = (class_1415)iterator.next();
			if (lv.method_6397()) {
				iterator.remove();
				this.method_80();
			}
		}
	}

	public List<class_1415> method_6436() {
		return this.field_6715;
	}

	public class_1415 method_6438(class_2338 arg, int i) {
		class_1415 lv = null;
		double d = Float.MAX_VALUE;

		for (class_1415 lv2 : this.field_6715) {
			double e = lv2.method_6382().method_10262(arg);
			if (!(e >= d)) {
				float f = (float)(i + lv2.method_6403());
				if (!(e > (double)(f * f))) {
					lv = lv2;
					d = e;
				}
			}
		}

		return lv;
	}

	private void method_6431() {
		if (!this.field_6718.isEmpty()) {
			this.method_6439((class_2338)this.field_6718.remove(0));
		}
	}

	private void method_6432() {
		for (int i = 0; i < this.field_6714.size(); i++) {
			class_1417 lv = (class_1417)this.field_6714.get(i);
			class_1415 lv2 = this.method_6438(lv.method_6429(), 32);
			if (lv2 == null) {
				lv2 = new class_1415(this.field_6717);
				this.field_6715.add(lv2);
				this.method_80();
			}

			lv2.method_6392(lv);
		}

		this.field_6714.clear();
	}

	private void method_6439(class_2338 arg) {
		int i = 16;
		int j = 4;
		int k = 16;
		class_2338.class_2339 lv = new class_2338.class_2339();

		for (int l = -16; l < 16; l++) {
			for (int m = -4; m < 4; m++) {
				for (int n = -16; n < 16; n++) {
					lv.method_10101(arg).method_10100(l, m, n);
					class_2680 lv2 = this.field_6717.method_8320(lv);
					if (this.method_6442(lv2)) {
						class_1417 lv3 = this.method_6444(lv);
						if (lv3 == null) {
							this.method_6443(lv2, lv);
						} else {
							lv3.method_6414(this.field_6716);
						}
					}
				}
			}
		}
	}

	@Nullable
	private class_1417 method_6444(class_2338 arg) {
		for (class_1417 lv : this.field_6714) {
			if (lv.method_6429().method_10263() == arg.method_10263()
				&& lv.method_6429().method_10260() == arg.method_10260()
				&& Math.abs(lv.method_6429().method_10264() - arg.method_10264()) <= 1) {
				return lv;
			}
		}

		for (class_1415 lv2 : this.field_6715) {
			class_1417 lv3 = lv2.method_6390(arg);
			if (lv3 != null) {
				return lv3;
			}
		}

		return null;
	}

	private void method_6443(class_2680 arg, class_2338 arg2) {
		class_2350 lv = arg.method_11654(class_2323.field_10938);
		class_2350 lv2 = lv.method_10153();
		int i = this.method_6437(arg2, lv, 5);
		int j = this.method_6437(arg2, lv2, i + 1);
		if (i != j) {
			this.field_6714.add(new class_1417(arg2, i < j ? lv : lv2, this.field_6716));
		}
	}

	private int method_6437(class_2338 arg, class_2350 arg2, int i) {
		int j = 0;

		for (int k = 1; k <= 5; k++) {
			if (this.field_6717.method_8311(arg.method_10079(arg2, k))) {
				if (++j >= i) {
					return j;
				}
			}
		}

		return j;
	}

	private boolean method_6441(class_2338 arg) {
		for (class_2338 lv : this.field_6718) {
			if (lv.equals(arg)) {
				return true;
			}
		}

		return false;
	}

	private boolean method_6442(class_2680 arg) {
		return arg.method_11614() instanceof class_2323 && arg.method_11620() == class_3614.field_15932;
	}

	@Override
	public void method_77(class_2487 arg) {
		this.field_6716 = arg.method_10550("Tick");
		class_2499 lv = arg.method_10554("Villages", 10);

		for (int i = 0; i < lv.size(); i++) {
			class_2487 lv2 = lv.method_10602(i);
			class_1415 lv3 = new class_1415();
			lv3.method_6410(lv2);
			this.field_6715.add(lv3);
		}
	}

	@Override
	public class_2487 method_75(class_2487 arg) {
		arg.method_10569("Tick", this.field_6716);
		class_2499 lv = new class_2499();

		for (class_1415 lv2 : this.field_6715) {
			class_2487 lv3 = new class_2487();
			lv2.method_6408(lv3);
			lv.method_10606(lv3);
		}

		arg.method_10566("Villages", lv);
		return arg;
	}

	public void method_16471() {
		for (class_1415 lv : this.field_6715) {
			int i = lv.method_16467();
			if (i > 0) {
				class_3765 lv2 = this.field_6717.method_16542().method_16541(i);
				if (lv2 != null) {
					lv2.method_16512(lv);
				} else {
					lv.method_16468(0);
				}
			}
		}
	}

	public static String method_6434(class_2869 arg) {
		return "villages" + arg.method_12460().method_12489();
	}
}
