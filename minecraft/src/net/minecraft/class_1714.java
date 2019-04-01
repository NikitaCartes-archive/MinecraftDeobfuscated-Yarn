package net.minecraft;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1714 extends class_1729<class_1715> {
	private final class_1715 field_7801 = new class_1715(this, 3, 3);
	private final class_1731 field_7800 = new class_1731();
	private final class_3914 field_7799;
	private final class_1657 field_7797;

	public class_1714(int i, class_1661 arg) {
		this(i, arg, class_3914.field_17304);
	}

	public class_1714(int i, class_1661 arg, class_3914 arg2) {
		super(class_3917.field_17333, i);
		this.field_7799 = arg2;
		this.field_7797 = arg.field_7546;
		this.method_7621(new class_1734(arg.field_7546, this.field_7801, this.field_7800, 0, 124, 35));

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 3; k++) {
				this.method_7621(new class_1735(this.field_7801, k + j * 3, 30 + k * 18, 17 + j * 18));
			}
		}

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.method_7621(new class_1735(arg, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.method_7621(new class_1735(arg, j, 8 + j * 18, 142));
		}
	}

	protected static void method_17399(int i, class_1937 arg, class_1657 arg2, class_1715 arg3, class_1731 arg4) {
		if (!arg.field_9236) {
			class_3222 lv = (class_3222)arg2;
			class_1799 lv2 = class_1799.field_8037;
			Optional<class_3955> optional = arg.method_8503().method_3772().method_8132(class_3956.field_17545, arg3, arg);
			if (optional.isPresent()) {
				class_3955 lv3 = (class_3955)optional.get();
				if (arg4.method_7665(arg, lv, lv3)) {
					lv2 = lv3.method_8116(arg3);
				}
			}

			arg4.method_5447(0, lv2);
			lv.field_13987.method_14364(new class_2653(i, 0, lv2));
		}
	}

	@Override
	public void method_7609(class_1263 arg) {
		this.field_7799.method_17393((argx, arg2) -> method_17399(this.field_7763, argx, this.field_7797, this.field_7801, this.field_7800));
	}

	@Override
	public void method_7654(class_1662 arg) {
		this.field_7801.method_7683(arg);
	}

	@Override
	public void method_7657() {
		this.field_7801.method_5448();
		this.field_7800.method_5448();
	}

	@Override
	public boolean method_7652(class_1860<? super class_1715> arg) {
		return arg.method_8115(this.field_7801, this.field_7797.field_6002);
	}

	@Override
	public void method_7595(class_1657 arg) {
		super.method_7595(arg);
		this.field_7799.method_17393((arg2, arg3) -> this.method_7607(arg, arg2, this.field_7801));
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return method_17695(this.field_7799, arg, class_2246.field_9980);
	}

	@Override
	public class_1799 method_7601(class_1657 arg, int i) {
		class_1799 lv = class_1799.field_8037;
		class_1735 lv2 = (class_1735)this.field_7761.get(i);
		if (lv2 != null && lv2.method_7681()) {
			class_1799 lv3 = lv2.method_7677();
			lv = lv3.method_7972();
			if (i == 0) {
				this.field_7799.method_17393((arg3, arg4) -> lv3.method_7909().method_7843(lv3, arg3, arg));
				if (!this.method_7616(lv3, 10, 46, true)) {
					return class_1799.field_8037;
				}

				lv2.method_7670(lv3, lv);
			} else if (i >= 10 && i < 37) {
				if (!this.method_7616(lv3, 37, 46, false)) {
					return class_1799.field_8037;
				}
			} else if (i >= 37 && i < 46) {
				if (!this.method_7616(lv3, 10, 37, false)) {
					return class_1799.field_8037;
				}
			} else if (!this.method_7616(lv3, 10, 46, false)) {
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

			class_1799 lv4 = lv2.method_7667(arg, lv3);
			if (i == 0) {
				arg.method_7328(lv4, false);
			}
		}

		return lv;
	}

	@Override
	public boolean method_7613(class_1799 arg, class_1735 arg2) {
		return arg2.field_7871 != this.field_7800 && super.method_7613(arg, arg2);
	}

	@Override
	public int method_7655() {
		return 0;
	}

	@Override
	public int method_7653() {
		return this.field_7801.method_17398();
	}

	@Override
	public int method_7656() {
		return this.field_7801.method_17397();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_7658() {
		return 10;
	}
}
