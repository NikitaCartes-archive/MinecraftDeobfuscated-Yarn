package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1725 implements class_1263 {
	private final class_1915 field_7844;
	private final class_2371<class_1799> field_7845 = class_2371.method_10213(3, class_1799.field_8037);
	@Nullable
	private class_1914 field_7843;
	private int field_7842;
	private int field_18668;

	public class_1725(class_1915 arg) {
		this.field_7844 = arg;
	}

	@Override
	public int method_5439() {
		return this.field_7845.size();
	}

	@Override
	public boolean method_5442() {
		for (class_1799 lv : this.field_7845) {
			if (!lv.method_7960()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public class_1799 method_5438(int i) {
		return this.field_7845.get(i);
	}

	@Override
	public class_1799 method_5434(int i, int j) {
		class_1799 lv = this.field_7845.get(i);
		if (i == 2 && !lv.method_7960()) {
			return class_1262.method_5430(this.field_7845, i, lv.method_7947());
		} else {
			class_1799 lv2 = class_1262.method_5430(this.field_7845, i, j);
			if (!lv2.method_7960() && this.method_7644(i)) {
				this.method_7645();
			}

			return lv2;
		}
	}

	private boolean method_7644(int i) {
		return i == 0 || i == 1;
	}

	@Override
	public class_1799 method_5441(int i) {
		return class_1262.method_5428(this.field_7845, i);
	}

	@Override
	public void method_5447(int i, class_1799 arg) {
		this.field_7845.set(i, arg);
		if (!arg.method_7960() && arg.method_7947() > this.method_5444()) {
			arg.method_7939(this.method_5444());
		}

		if (this.method_7644(i)) {
			this.method_7645();
		}
	}

	@Override
	public boolean method_5443(class_1657 arg) {
		return this.field_7844.method_8257() == arg;
	}

	@Override
	public void method_5431() {
		this.method_7645();
	}

	public void method_7645() {
		this.field_7843 = null;
		class_1799 lv;
		class_1799 lv2;
		if (this.field_7845.get(0).method_7960()) {
			lv = this.field_7845.get(1);
			lv2 = class_1799.field_8037;
		} else {
			lv = this.field_7845.get(0);
			lv2 = this.field_7845.get(1);
		}

		if (lv.method_7960()) {
			this.method_5447(2, class_1799.field_8037);
			this.field_18668 = 0;
		} else {
			class_1916 lv3 = this.field_7844.method_8264();
			if (!lv3.isEmpty()) {
				class_1914 lv4 = lv3.method_8267(lv, lv2, this.field_7842);
				if (lv4 == null || lv4.method_8255()) {
					this.field_7843 = lv4;
					lv4 = lv3.method_8267(lv2, lv, this.field_7842);
				}

				if (lv4 != null && !lv4.method_8255()) {
					this.field_7843 = lv4;
					this.method_5447(2, lv4.method_18019());
					this.field_18668 = lv4.method_19279();
				} else {
					this.method_5447(2, class_1799.field_8037);
					this.field_18668 = 0;
				}
			}

			this.field_7844.method_8258(this.method_5438(2));
		}
	}

	@Nullable
	public class_1914 method_7642() {
		return this.field_7843;
	}

	public void method_7643(int i) {
		this.field_7842 = i;
		this.method_7645();
	}

	@Override
	public void method_5448() {
		this.field_7845.clear();
	}

	@Environment(EnvType.CLIENT)
	public int method_19252() {
		return this.field_18668;
	}
}
