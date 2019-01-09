package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1723 extends class_1729<class_1723> {
	private static final String[] field_7829 = new String[]{
		"item/empty_armor_slot_boots", "item/empty_armor_slot_leggings", "item/empty_armor_slot_chestplate", "item/empty_armor_slot_helmet"
	};
	private static final class_1304[] field_7832 = new class_1304[]{class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166};
	private final class_1715 field_7831 = new class_1715(this, 2, 2);
	private final class_1731 field_7830 = new class_1731();
	public final boolean field_7827;
	private final class_1657 field_7828;

	public class_1723(class_1661 arg, boolean bl, class_1657 arg2) {
		super(0);
		this.field_7827 = bl;
		this.field_7828 = arg2;
		this.method_7621(new class_1734(arg.field_7546, this.field_7831, this.field_7830, 0, 154, 28));

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				this.method_7621(new class_1735(this.field_7831, j + i * 2, 98 + j * 18, 18 + i * 18));
			}
		}

		for (int i = 0; i < 4; i++) {
			final class_1304 lv = field_7832[i];
			this.method_7621(new class_1735(arg, 39 - i, 8, 8 + i * 18) {
				@Override
				public int method_7675() {
					return 1;
				}

				@Override
				public boolean method_7680(class_1799 arg) {
					return lv == class_1308.method_5953(arg);
				}

				@Override
				public boolean method_7674(class_1657 arg) {
					class_1799 lv = this.method_7677();
					return !lv.method_7960() && !arg.method_7337() && class_1890.method_8224(lv) ? false : super.method_7674(arg);
				}

				@Nullable
				@Environment(EnvType.CLIENT)
				@Override
				public String method_7679() {
					return class_1723.field_7829[lv.method_5927()];
				}
			});
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.method_7621(new class_1735(arg, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.method_7621(new class_1735(arg, i, 8 + i * 18, 142));
		}

		this.method_7621(new class_1735(arg, 40, 77, 62) {
			@Nullable
			@Environment(EnvType.CLIENT)
			@Override
			public String method_7679() {
				return "item/empty_armor_slot_shield";
			}
		});
	}

	@Override
	public class_3917<class_1723> method_17358() {
		throw new UnsupportedOperationException("This menu can't be created in normal way");
	}

	@Override
	public void method_7654(class_1662 arg) {
		this.field_7831.method_7683(arg);
	}

	@Override
	public void method_7657() {
		this.field_7830.method_5448();
		this.field_7831.method_5448();
	}

	@Override
	public boolean method_7652(class_1860 arg) {
		return arg.method_8115(this.field_7831, this.field_7828.field_6002);
	}

	@Override
	public void method_7609(class_1263 arg) {
		this.method_7599(this.field_7828.field_6002, this.field_7828, this.field_7831, this.field_7830);
	}

	@Override
	public void method_7595(class_1657 arg) {
		super.method_7595(arg);
		this.field_7830.method_5448();
		if (!arg.field_6002.field_9236) {
			this.method_7607(arg, arg.field_6002, this.field_7831);
		}
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return true;
	}

	@Override
	public class_1799 method_7601(class_1657 arg, int i) {
		class_1799 lv = class_1799.field_8037;
		class_1735 lv2 = (class_1735)this.field_7761.get(i);
		if (lv2 != null && lv2.method_7681()) {
			class_1799 lv3 = lv2.method_7677();
			lv = lv3.method_7972();
			class_1304 lv4 = class_1308.method_5953(lv);
			if (i == 0) {
				if (!this.method_7616(lv3, 9, 45, true)) {
					return class_1799.field_8037;
				}

				lv2.method_7670(lv3, lv);
			} else if (i >= 1 && i < 5) {
				if (!this.method_7616(lv3, 9, 45, false)) {
					return class_1799.field_8037;
				}
			} else if (i >= 5 && i < 9) {
				if (!this.method_7616(lv3, 9, 45, false)) {
					return class_1799.field_8037;
				}
			} else if (lv4.method_5925() == class_1304.class_1305.field_6178 && !((class_1735)this.field_7761.get(8 - lv4.method_5927())).method_7681()) {
				int j = 8 - lv4.method_5927();
				if (!this.method_7616(lv3, j, j + 1, false)) {
					return class_1799.field_8037;
				}
			} else if (lv4 == class_1304.field_6171 && !((class_1735)this.field_7761.get(45)).method_7681()) {
				if (!this.method_7616(lv3, 45, 46, false)) {
					return class_1799.field_8037;
				}
			} else if (i >= 9 && i < 36) {
				if (!this.method_7616(lv3, 36, 45, false)) {
					return class_1799.field_8037;
				}
			} else if (i >= 36 && i < 45) {
				if (!this.method_7616(lv3, 9, 36, false)) {
					return class_1799.field_8037;
				}
			} else if (!this.method_7616(lv3, 9, 45, false)) {
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

			class_1799 lv5 = lv2.method_7667(arg, lv3);
			if (i == 0) {
				arg.method_7328(lv5, false);
			}
		}

		return lv;
	}

	@Override
	public boolean method_7613(class_1799 arg, class_1735 arg2) {
		return arg2.field_7871 != this.field_7830 && super.method_7613(arg, arg2);
	}

	@Override
	public int method_7655() {
		return 0;
	}

	@Override
	public int method_7653() {
		return this.field_7831.method_17398();
	}

	@Override
	public int method_7656() {
		return this.field_7831.method_17397();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_7658() {
		return 5;
	}
}
