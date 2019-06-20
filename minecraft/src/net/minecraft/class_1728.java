package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1728 extends class_1703 {
	private final class_1915 field_7863;
	private final class_1725 field_7861;
	@Environment(EnvType.CLIENT)
	private int field_18669;
	@Environment(EnvType.CLIENT)
	private boolean field_18670;
	@Environment(EnvType.CLIENT)
	private boolean field_19358;

	public class_1728(int i, class_1661 arg) {
		this(i, arg, new class_1645(arg.field_7546));
	}

	public class_1728(int i, class_1661 arg, class_1915 arg2) {
		super(class_3917.field_17340, i);
		this.field_7863 = arg2;
		this.field_7861 = new class_1725(arg2);
		this.method_7621(new class_1735(this.field_7861, 0, 136, 37));
		this.method_7621(new class_1735(this.field_7861, 1, 162, 37));
		this.method_7621(new class_1727(arg.field_7546, arg2, this.field_7861, 2, 220, 37));

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.method_7621(new class_1735(arg, k + j * 9 + 9, 108 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.method_7621(new class_1735(arg, j, 108 + j * 18, 142));
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_19253(boolean bl) {
		this.field_18670 = bl;
	}

	@Override
	public void method_7609(class_1263 arg) {
		this.field_7861.method_7645();
		super.method_7609(arg);
	}

	public void method_7650(int i) {
		this.field_7861.method_7643(i);
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return this.field_7863.method_8257() == arg;
	}

	@Environment(EnvType.CLIENT)
	public int method_19254() {
		return this.field_7863.method_19269();
	}

	@Environment(EnvType.CLIENT)
	public int method_19256() {
		return this.field_7861.method_19252();
	}

	@Environment(EnvType.CLIENT)
	public void method_19255(int i) {
		this.field_7863.method_19271(i);
	}

	@Environment(EnvType.CLIENT)
	public int method_19258() {
		return this.field_18669;
	}

	@Environment(EnvType.CLIENT)
	public void method_19257(int i) {
		this.field_18669 = i;
	}

	@Environment(EnvType.CLIENT)
	public void method_20700(boolean bl) {
		this.field_19358 = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_20701() {
		return this.field_19358;
	}

	@Override
	public boolean method_7613(class_1799 arg, class_1735 arg2) {
		return false;
	}

	@Override
	public class_1799 method_7601(class_1657 arg, int i) {
		class_1799 lv = class_1799.field_8037;
		class_1735 lv2 = (class_1735)this.field_7761.get(i);
		if (lv2 != null && lv2.method_7681()) {
			class_1799 lv3 = lv2.method_7677();
			lv = lv3.method_7972();
			if (i == 2) {
				if (!this.method_7616(lv3, 3, 39, true)) {
					return class_1799.field_8037;
				}

				lv2.method_7670(lv3, lv);
				this.method_20595();
			} else if (i != 0 && i != 1) {
				if (i >= 3 && i < 30) {
					if (!this.method_7616(lv3, 30, 39, false)) {
						return class_1799.field_8037;
					}
				} else if (i >= 30 && i < 39 && !this.method_7616(lv3, 3, 30, false)) {
					return class_1799.field_8037;
				}
			} else if (!this.method_7616(lv3, 3, 39, false)) {
				return class_1799.field_8037;
			}

			if (lv3.method_7960()) {
				lv2.method_7673(class_1799.field_8037);
			} else {
				lv2.method_7668();
			}

			if (lv3.method_7947() == lv.method_7947()) {
				return class_1799.field_8037;
			}

			lv2.method_7667(arg, lv3);
		}

		return lv;
	}

	private void method_20595() {
		if (!this.field_7863.method_8260().field_9236) {
			class_1297 lv = (class_1297)this.field_7863;
			this.field_7863
				.method_8260()
				.method_8486(lv.field_5987, lv.field_6010, lv.field_6035, this.field_7863.method_18010(), class_3419.field_15254, 1.0F, 1.0F, false);
		}
	}

	@Override
	public void method_7595(class_1657 arg) {
		super.method_7595(arg);
		this.field_7863.method_8259(null);
		if (!this.field_7863.method_8260().field_9236) {
			if (!arg.method_5805() || arg instanceof class_3222 && ((class_3222)arg).method_14239()) {
				class_1799 lv = this.field_7861.method_5441(0);
				if (!lv.method_7960()) {
					arg.method_7328(lv, false);
				}

				lv = this.field_7861.method_5441(1);
				if (!lv.method_7960()) {
					arg.method_7328(lv, false);
				}
			} else {
				arg.field_7514.method_7398(arg.field_6002, this.field_7861.method_5441(0));
				arg.field_7514.method_7398(arg.field_6002, this.field_7861.method_5441(1));
			}
		}
	}

	public void method_20215(int i) {
		if (this.method_17438().size() > i) {
			class_1799 lv = this.field_7861.method_5438(0);
			if (!lv.method_7960()) {
				if (!this.method_7616(lv, 3, 39, true)) {
					return;
				}

				this.field_7861.method_5447(0, lv);
			}

			class_1799 lv2 = this.field_7861.method_5438(1);
			if (!lv2.method_7960()) {
				if (!this.method_7616(lv2, 3, 39, true)) {
					return;
				}

				this.field_7861.method_5447(1, lv2);
			}

			if (this.field_7861.method_5438(0).method_7960() && this.field_7861.method_5438(1).method_7960()) {
				class_1799 lv3 = ((class_1914)this.method_17438().get(i)).method_19272();
				this.method_20214(0, lv3);
				class_1799 lv4 = ((class_1914)this.method_17438().get(i)).method_8247();
				this.method_20214(1, lv4);
			}
		}
	}

	private void method_20214(int i, class_1799 arg) {
		if (!arg.method_7960()) {
			for (int j = 3; j < 39; j++) {
				class_1799 lv = ((class_1735)this.field_7761.get(j)).method_7677();
				if (!lv.method_7960() && this.method_20213(arg, lv)) {
					class_1799 lv2 = this.field_7861.method_5438(i);
					int k = lv2.method_7960() ? 0 : lv2.method_7947();
					int l = Math.min(arg.method_7914() - k, lv.method_7947());
					class_1799 lv3 = lv.method_7972();
					int m = k + l;
					lv.method_7934(l);
					lv3.method_7939(m);
					this.field_7861.method_5447(i, lv3);
					if (m >= arg.method_7914()) {
						break;
					}
				}
			}
		}
	}

	private boolean method_20213(class_1799 arg, class_1799 arg2) {
		return arg.method_7909() == arg2.method_7909() && class_1799.method_7975(arg, arg2);
	}

	@Environment(EnvType.CLIENT)
	public void method_17437(class_1916 arg) {
		this.field_7863.method_8261(arg);
	}

	public class_1916 method_17438() {
		return this.field_7863.method_8264();
	}

	@Environment(EnvType.CLIENT)
	public boolean method_19259() {
		return this.field_18670;
	}
}
