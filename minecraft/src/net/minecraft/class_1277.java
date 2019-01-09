package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;

public class class_1277 implements class_1263, class_1737 {
	private final int field_5831;
	private final class_2371<class_1799> field_5828;
	private List<class_1265> field_5829;

	public class_1277(int i) {
		this.field_5831 = i;
		this.field_5828 = class_2371.method_10213(i, class_1799.field_8037);
	}

	public void method_5489(class_1265 arg) {
		if (this.field_5829 == null) {
			this.field_5829 = Lists.<class_1265>newArrayList();
		}

		this.field_5829.add(arg);
	}

	public void method_5488(class_1265 arg) {
		this.field_5829.remove(arg);
	}

	@Override
	public class_1799 method_5438(int i) {
		return i >= 0 && i < this.field_5828.size() ? this.field_5828.get(i) : class_1799.field_8037;
	}

	@Override
	public class_1799 method_5434(int i, int j) {
		class_1799 lv = class_1262.method_5430(this.field_5828, i, j);
		if (!lv.method_7960()) {
			this.method_5431();
		}

		return lv;
	}

	public class_1799 method_5491(class_1799 arg) {
		class_1799 lv = arg.method_7972();

		for (int i = 0; i < this.field_5831; i++) {
			class_1799 lv2 = this.method_5438(i);
			if (lv2.method_7960()) {
				this.method_5447(i, lv);
				this.method_5431();
				return class_1799.field_8037;
			}

			if (class_1799.method_7984(lv2, lv)) {
				int j = Math.min(this.method_5444(), lv2.method_7914());
				int k = Math.min(lv.method_7947(), j - lv2.method_7947());
				if (k > 0) {
					lv2.method_7933(k);
					lv.method_7934(k);
					if (lv.method_7960()) {
						this.method_5431();
						return class_1799.field_8037;
					}
				}
			}
		}

		if (lv.method_7947() != arg.method_7947()) {
			this.method_5431();
		}

		return lv;
	}

	@Override
	public class_1799 method_5441(int i) {
		class_1799 lv = this.field_5828.get(i);
		if (lv.method_7960()) {
			return class_1799.field_8037;
		} else {
			this.field_5828.set(i, class_1799.field_8037);
			return lv;
		}
	}

	@Override
	public void method_5447(int i, class_1799 arg) {
		this.field_5828.set(i, arg);
		if (!arg.method_7960() && arg.method_7947() > this.method_5444()) {
			arg.method_7939(this.method_5444());
		}

		this.method_5431();
	}

	@Override
	public int method_5439() {
		return this.field_5831;
	}

	@Override
	public boolean method_5442() {
		for (class_1799 lv : this.field_5828) {
			if (!lv.method_7960()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void method_5431() {
		if (this.field_5829 != null) {
			for (class_1265 lv : this.field_5829) {
				lv.method_5453(this);
			}
		}
	}

	@Override
	public boolean method_5443(class_1657 arg) {
		return true;
	}

	@Override
	public void method_5448() {
		this.field_5828.clear();
	}

	@Override
	public void method_7683(class_1662 arg) {
		for (class_1799 lv : this.field_5828) {
			arg.method_7400(lv);
		}
	}
}
