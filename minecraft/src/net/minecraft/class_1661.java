package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1661 implements class_1263, class_1275 {
	public final class_2371<class_1799> field_7547 = class_2371.method_10213(36, class_1799.field_8037);
	public final class_2371<class_1799> field_7548 = class_2371.method_10213(4, class_1799.field_8037);
	public final class_2371<class_1799> field_7544 = class_2371.method_10213(1, class_1799.field_8037);
	private final List<class_2371<class_1799>> field_7543 = ImmutableList.of(this.field_7547, this.field_7548, this.field_7544);
	public int field_7545;
	public final class_1657 field_7546;
	private class_1799 field_7549 = class_1799.field_8037;
	private int field_7542;

	public class_1661(class_1657 arg) {
		this.field_7546 = arg;
	}

	public class_1799 method_7391() {
		return method_7380(this.field_7545) ? this.field_7547.get(this.field_7545) : class_1799.field_8037;
	}

	public static int method_7368() {
		return 9;
	}

	private boolean method_7393(class_1799 arg, class_1799 arg2) {
		return !arg.method_7960()
			&& this.method_7392(arg, arg2)
			&& arg.method_7946()
			&& arg.method_7947() < arg.method_7914()
			&& arg.method_7947() < this.method_5444();
	}

	private boolean method_7392(class_1799 arg, class_1799 arg2) {
		return arg.method_7909() == arg2.method_7909() && class_1799.method_7975(arg, arg2);
	}

	public int method_7376() {
		for (int i = 0; i < this.field_7547.size(); i++) {
			if (this.field_7547.get(i).method_7960()) {
				return i;
			}
		}

		return -1;
	}

	@Environment(EnvType.CLIENT)
	public void method_7374(class_1799 arg) {
		int i = this.method_7395(arg);
		if (method_7380(i)) {
			this.field_7545 = i;
		} else {
			if (i == -1) {
				this.field_7545 = this.method_7386();
				if (!this.field_7547.get(this.field_7545).method_7960()) {
					int j = this.method_7376();
					if (j != -1) {
						this.field_7547.set(j, this.field_7547.get(this.field_7545));
					}
				}

				this.field_7547.set(this.field_7545, arg);
			} else {
				this.method_7365(i);
			}
		}
	}

	public void method_7365(int i) {
		this.field_7545 = this.method_7386();
		class_1799 lv = this.field_7547.get(this.field_7545);
		this.field_7547.set(this.field_7545, this.field_7547.get(i));
		this.field_7547.set(i, lv);
	}

	public static boolean method_7380(int i) {
		return i >= 0 && i < 9;
	}

	@Environment(EnvType.CLIENT)
	public int method_7395(class_1799 arg) {
		for (int i = 0; i < this.field_7547.size(); i++) {
			if (!this.field_7547.get(i).method_7960() && this.method_7392(arg, this.field_7547.get(i))) {
				return i;
			}
		}

		return -1;
	}

	public int method_7371(class_1799 arg) {
		for (int i = 0; i < this.field_7547.size(); i++) {
			class_1799 lv = this.field_7547.get(i);
			if (!this.field_7547.get(i).method_7960()
				&& this.method_7392(arg, this.field_7547.get(i))
				&& !this.field_7547.get(i).method_7986()
				&& !lv.method_7942()
				&& !lv.method_7938()) {
				return i;
			}
		}

		return -1;
	}

	public int method_7386() {
		for (int i = 0; i < 9; i++) {
			int j = (this.field_7545 + i) % 9;
			if (this.field_7547.get(j).method_7960()) {
				return j;
			}
		}

		for (int ix = 0; ix < 9; ix++) {
			int j = (this.field_7545 + ix) % 9;
			if (!this.field_7547.get(j).method_7942()) {
				return j;
			}
		}

		return this.field_7545;
	}

	@Environment(EnvType.CLIENT)
	public void method_7373(double d) {
		if (d > 0.0) {
			d = 1.0;
		}

		if (d < 0.0) {
			d = -1.0;
		}

		this.field_7545 = (int)((double)this.field_7545 - d);

		while (this.field_7545 < 0) {
			this.field_7545 += 9;
		}

		while (this.field_7545 >= 9) {
			this.field_7545 -= 9;
		}
	}

	public int method_7369(Predicate<class_1799> predicate, int i) {
		int j = 0;

		for (int k = 0; k < this.method_5439(); k++) {
			class_1799 lv = this.method_5438(k);
			if (!lv.method_7960() && predicate.test(lv)) {
				int l = i <= 0 ? lv.method_7947() : Math.min(i - j, lv.method_7947());
				j += l;
				if (i != 0) {
					lv.method_7934(l);
					if (lv.method_7960()) {
						this.method_5447(k, class_1799.field_8037);
					}

					if (i > 0 && j >= i) {
						return j;
					}
				}
			}
		}

		if (!this.field_7549.method_7960() && predicate.test(this.field_7549)) {
			int kx = i <= 0 ? this.field_7549.method_7947() : Math.min(i - j, this.field_7549.method_7947());
			j += kx;
			if (i != 0) {
				this.field_7549.method_7934(kx);
				if (this.field_7549.method_7960()) {
					this.field_7549 = class_1799.field_8037;
				}

				if (i > 0 && j >= i) {
					return j;
				}
			}
		}

		return j;
	}

	private int method_7366(class_1799 arg) {
		int i = this.method_7390(arg);
		if (i == -1) {
			i = this.method_7376();
		}

		return i == -1 ? arg.method_7947() : this.method_7385(i, arg);
	}

	private int method_7385(int i, class_1799 arg) {
		class_1792 lv = arg.method_7909();
		int j = arg.method_7947();
		class_1799 lv2 = this.method_5438(i);
		if (lv2.method_7960()) {
			lv2 = new class_1799(lv, 0);
			if (arg.method_7985()) {
				lv2.method_7980(arg.method_7969().method_10553());
			}

			this.method_5447(i, lv2);
		}

		int k = j;
		if (j > lv2.method_7914() - lv2.method_7947()) {
			k = lv2.method_7914() - lv2.method_7947();
		}

		if (k > this.method_5444() - lv2.method_7947()) {
			k = this.method_5444() - lv2.method_7947();
		}

		if (k == 0) {
			return j;
		} else {
			j -= k;
			lv2.method_7933(k);
			lv2.method_7912(5);
			return j;
		}
	}

	public int method_7390(class_1799 arg) {
		if (this.method_7393(this.method_5438(this.field_7545), arg)) {
			return this.field_7545;
		} else if (this.method_7393(this.method_5438(40), arg)) {
			return 40;
		} else {
			for (int i = 0; i < this.field_7547.size(); i++) {
				if (this.method_7393(this.field_7547.get(i), arg)) {
					return i;
				}
			}

			return -1;
		}
	}

	public void method_7381() {
		for (class_2371<class_1799> lv : this.field_7543) {
			for (int i = 0; i < lv.size(); i++) {
				if (!lv.get(i).method_7960()) {
					lv.get(i).method_7917(this.field_7546.field_6002, this.field_7546, i, this.field_7545 == i);
				}
			}
		}
	}

	public boolean method_7394(class_1799 arg) {
		return this.method_7367(-1, arg);
	}

	public boolean method_7367(int i, class_1799 arg) {
		if (arg.method_7960()) {
			return false;
		} else {
			try {
				if (arg.method_7986()) {
					if (i == -1) {
						i = this.method_7376();
					}

					if (i >= 0) {
						this.field_7547.set(i, arg.method_7972());
						this.field_7547.get(i).method_7912(5);
						arg.method_7939(0);
						return true;
					} else if (this.field_7546.field_7503.field_7477) {
						arg.method_7939(0);
						return true;
					} else {
						return false;
					}
				} else {
					int j;
					do {
						j = arg.method_7947();
						if (i == -1) {
							arg.method_7939(this.method_7366(arg));
						} else {
							arg.method_7939(this.method_7385(i, arg));
						}
					} while (!arg.method_7960() && arg.method_7947() < j);

					if (arg.method_7947() == j && this.field_7546.field_7503.field_7477) {
						arg.method_7939(0);
						return true;
					} else {
						return arg.method_7947() < j;
					}
				}
			} catch (Throwable var6) {
				class_128 lv = class_128.method_560(var6, "Adding item to inventory");
				class_129 lv2 = lv.method_562("Item being added");
				lv2.method_578("Item ID", class_1792.method_7880(arg.method_7909()));
				lv2.method_578("Item data", arg.method_7919());
				lv2.method_577("Item name", () -> arg.method_7964().getString());
				throw new class_148(lv);
			}
		}
	}

	public void method_7398(class_1937 arg, class_1799 arg2) {
		if (!arg.field_9236) {
			while (!arg2.method_7960()) {
				int i = this.method_7390(arg2);
				if (i == -1) {
					i = this.method_7376();
				}

				if (i == -1) {
					this.field_7546.method_7328(arg2, false);
					break;
				}

				int j = arg2.method_7914() - this.method_5438(i).method_7947();
				if (this.method_7367(i, arg2.method_7971(j))) {
					((class_3222)this.field_7546).field_13987.method_14364(new class_2653(-2, i, this.method_5438(i)));
				}
			}
		}
	}

	@Override
	public class_1799 method_5434(int i, int j) {
		List<class_1799> list = null;

		for (class_2371<class_1799> lv : this.field_7543) {
			if (i < lv.size()) {
				list = lv;
				break;
			}

			i -= lv.size();
		}

		return list != null && !((class_1799)list.get(i)).method_7960() ? class_1262.method_5430(list, i, j) : class_1799.field_8037;
	}

	public void method_7378(class_1799 arg) {
		for (class_2371<class_1799> lv : this.field_7543) {
			for (int i = 0; i < lv.size(); i++) {
				if (lv.get(i) == arg) {
					lv.set(i, class_1799.field_8037);
					break;
				}
			}
		}
	}

	@Override
	public class_1799 method_5441(int i) {
		class_2371<class_1799> lv = null;

		for (class_2371<class_1799> lv2 : this.field_7543) {
			if (i < lv2.size()) {
				lv = lv2;
				break;
			}

			i -= lv2.size();
		}

		if (lv != null && !lv.get(i).method_7960()) {
			class_1799 lv3 = lv.get(i);
			lv.set(i, class_1799.field_8037);
			return lv3;
		} else {
			return class_1799.field_8037;
		}
	}

	@Override
	public void method_5447(int i, class_1799 arg) {
		class_2371<class_1799> lv = null;

		for (class_2371<class_1799> lv2 : this.field_7543) {
			if (i < lv2.size()) {
				lv = lv2;
				break;
			}

			i -= lv2.size();
		}

		if (lv != null) {
			lv.set(i, arg);
		}
	}

	public float method_7370(class_2680 arg) {
		return this.field_7547.get(this.field_7545).method_7924(arg);
	}

	public class_2499 method_7384(class_2499 arg) {
		for (int i = 0; i < this.field_7547.size(); i++) {
			if (!this.field_7547.get(i).method_7960()) {
				class_2487 lv = new class_2487();
				lv.method_10567("Slot", (byte)i);
				this.field_7547.get(i).method_7953(lv);
				arg.add(lv);
			}
		}

		for (int ix = 0; ix < this.field_7548.size(); ix++) {
			if (!this.field_7548.get(ix).method_7960()) {
				class_2487 lv = new class_2487();
				lv.method_10567("Slot", (byte)(ix + 100));
				this.field_7548.get(ix).method_7953(lv);
				arg.add(lv);
			}
		}

		for (int ixx = 0; ixx < this.field_7544.size(); ixx++) {
			if (!this.field_7544.get(ixx).method_7960()) {
				class_2487 lv = new class_2487();
				lv.method_10567("Slot", (byte)(ixx + 150));
				this.field_7544.get(ixx).method_7953(lv);
				arg.add(lv);
			}
		}

		return arg;
	}

	public void method_7397(class_2499 arg) {
		this.field_7547.clear();
		this.field_7548.clear();
		this.field_7544.clear();

		for (int i = 0; i < arg.size(); i++) {
			class_2487 lv = arg.method_10602(i);
			int j = lv.method_10571("Slot") & 255;
			class_1799 lv2 = class_1799.method_7915(lv);
			if (!lv2.method_7960()) {
				if (j >= 0 && j < this.field_7547.size()) {
					this.field_7547.set(j, lv2);
				} else if (j >= 100 && j < this.field_7548.size() + 100) {
					this.field_7548.set(j - 100, lv2);
				} else if (j >= 150 && j < this.field_7544.size() + 150) {
					this.field_7544.set(j - 150, lv2);
				}
			}
		}
	}

	@Override
	public int method_5439() {
		return this.field_7547.size() + this.field_7548.size() + this.field_7544.size();
	}

	@Override
	public boolean method_5442() {
		for (class_1799 lv : this.field_7547) {
			if (!lv.method_7960()) {
				return false;
			}
		}

		for (class_1799 lvx : this.field_7548) {
			if (!lvx.method_7960()) {
				return false;
			}
		}

		for (class_1799 lvxx : this.field_7544) {
			if (!lvxx.method_7960()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public class_1799 method_5438(int i) {
		List<class_1799> list = null;

		for (class_2371<class_1799> lv : this.field_7543) {
			if (i < lv.size()) {
				list = lv;
				break;
			}

			i -= lv.size();
		}

		return list == null ? class_1799.field_8037 : (class_1799)list.get(i);
	}

	@Override
	public class_2561 method_5477() {
		return new class_2588("container.inventory");
	}

	public boolean method_7383(class_2680 arg) {
		return this.method_5438(this.field_7545).method_7951(arg);
	}

	@Environment(EnvType.CLIENT)
	public class_1799 method_7372(int i) {
		return this.field_7548.get(i);
	}

	public void method_7375(float f) {
		if (!(f <= 0.0F)) {
			f /= 4.0F;
			if (f < 1.0F) {
				f = 1.0F;
			}

			for (int i = 0; i < this.field_7548.size(); i++) {
				class_1799 lv = this.field_7548.get(i);
				if (lv.method_7909() instanceof class_1738) {
					int j = i;
					lv.method_7956((int)f, this.field_7546, arg -> arg.method_20235(class_1304.method_20234(class_1304.class_1305.field_6178, j)));
				}
			}
		}
	}

	public void method_7388() {
		for (List<class_1799> list : this.field_7543) {
			for (int i = 0; i < list.size(); i++) {
				class_1799 lv = (class_1799)list.get(i);
				if (!lv.method_7960()) {
					this.field_7546.method_7329(lv, true, false);
					list.set(i, class_1799.field_8037);
				}
			}
		}
	}

	@Override
	public void method_5431() {
		this.field_7542++;
	}

	@Environment(EnvType.CLIENT)
	public int method_7364() {
		return this.field_7542;
	}

	public void method_7396(class_1799 arg) {
		this.field_7549 = arg;
	}

	public class_1799 method_7399() {
		return this.field_7549;
	}

	@Override
	public boolean method_5443(class_1657 arg) {
		return this.field_7546.field_5988 ? false : !(arg.method_5858(this.field_7546) > 64.0);
	}

	public boolean method_7379(class_1799 arg) {
		for (List<class_1799> list : this.field_7543) {
			for (class_1799 lv : list) {
				if (!lv.method_7960() && lv.method_7962(arg)) {
					return true;
				}
			}
		}

		return false;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7382(class_3494<class_1792> arg) {
		for (List<class_1799> list : this.field_7543) {
			for (class_1799 lv : list) {
				if (!lv.method_7960() && arg.method_15141(lv.method_7909())) {
					return true;
				}
			}
		}

		return false;
	}

	public void method_7377(class_1661 arg) {
		for (int i = 0; i < this.method_5439(); i++) {
			this.method_5447(i, arg.method_5438(i));
		}

		this.field_7545 = arg.field_7545;
	}

	@Override
	public void method_5448() {
		for (List<class_1799> list : this.field_7543) {
			list.clear();
		}
	}

	public void method_7387(class_1662 arg) {
		for (class_1799 lv : this.field_7547) {
			arg.method_7404(lv);
		}
	}
}
