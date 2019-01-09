package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1704 extends class_1703 {
	private final class_1263 field_17287 = new class_1277(1) {
		@Override
		public boolean method_5437(int i, class_1799 arg) {
			return arg.method_7909() == class_1802.field_8687
				|| arg.method_7909() == class_1802.field_8477
				|| arg.method_7909() == class_1802.field_8695
				|| arg.method_7909() == class_1802.field_8620;
		}

		@Override
		public int method_5444() {
			return 1;
		}
	};
	private final class_1704.class_1705 field_17288;
	private final class_3914 field_17289;
	private final class_3913 field_17290;

	public class_1704(int i, class_1263 arg) {
		this(i, arg, new class_3919(3), class_3914.field_17304);
	}

	public class_1704(int i, class_1263 arg, class_3913 arg2, class_3914 arg3) {
		super(i);
		method_17361(arg2, 3);
		this.field_17290 = arg2;
		this.field_17289 = arg3;
		this.field_17288 = new class_1704.class_1705(this.field_17287, 0, 136, 110);
		this.method_7621(this.field_17288);
		this.method_17360(arg2);
		int j = 36;
		int k = 137;

		for (int l = 0; l < 3; l++) {
			for (int m = 0; m < 9; m++) {
				this.method_7621(new class_1735(arg, m + l * 9 + 9, 36 + m * 18, 137 + l * 18));
			}
		}

		for (int l = 0; l < 9; l++) {
			this.method_7621(new class_1735(arg, l, 36 + l * 18, 195));
		}
	}

	@Override
	public class_3917<class_1704> method_17358() {
		return class_3917.field_17330;
	}

	@Override
	public void method_7595(class_1657 arg) {
		super.method_7595(arg);
		if (!arg.field_6002.field_9236) {
			class_1799 lv = this.field_17288.method_7671(this.field_17288.method_7675());
			if (!lv.method_7960()) {
				arg.method_7328(lv, false);
			}
		}
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return this.field_17289
			.method_17396(
				(arg2, arg3) -> arg2.method_8320(arg3).method_11614() != class_2246.field_10327
						? false
						: arg.method_5649((double)arg3.method_10263() + 0.5, (double)arg3.method_10264() + 0.5, (double)arg3.method_10260() + 0.5) <= 64.0,
				true
			);
	}

	@Override
	public void method_7606(int i, int j) {
		super.method_7606(i, j);
		this.method_7623();
	}

	@Override
	public class_1799 method_7601(class_1657 arg, int i) {
		class_1799 lv = class_1799.field_8037;
		class_1735 lv2 = (class_1735)this.field_7761.get(i);
		if (lv2 != null && lv2.method_7681()) {
			class_1799 lv3 = lv2.method_7677();
			lv = lv3.method_7972();
			if (i == 0) {
				if (!this.method_7616(lv3, 1, 37, true)) {
					return class_1799.field_8037;
				}

				lv2.method_7670(lv3, lv);
			} else if (!this.field_17288.method_7681() && this.field_17288.method_7680(lv3) && lv3.method_7947() == 1) {
				if (!this.method_7616(lv3, 0, 1, false)) {
					return class_1799.field_8037;
				}
			} else if (i >= 1 && i < 28) {
				if (!this.method_7616(lv3, 28, 37, false)) {
					return class_1799.field_8037;
				}
			} else if (i >= 28 && i < 37) {
				if (!this.method_7616(lv3, 1, 28, false)) {
					return class_1799.field_8037;
				}
			} else if (!this.method_7616(lv3, 1, 37, false)) {
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

	@Environment(EnvType.CLIENT)
	public int method_17373() {
		return this.field_17290.method_17390(0);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_1291 method_17374() {
		return class_1291.method_5569(this.field_17290.method_17390(1));
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_1291 method_17375() {
		return class_1291.method_5569(this.field_17290.method_17390(2));
	}

	public void method_17372(int i, int j) {
		if (this.field_17288.method_7681()) {
			this.field_17290.method_17391(1, i);
			this.field_17290.method_17391(2, j);
			this.field_17288.method_7671(1);
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean method_17376() {
		return !this.field_17287.method_5438(0).method_7960();
	}

	class class_1705 extends class_1735 {
		public class_1705(class_1263 arg2, int i, int j, int k) {
			super(arg2, i, j, k);
		}

		@Override
		public boolean method_7680(class_1799 arg) {
			class_1792 lv = arg.method_7909();
			return lv == class_1802.field_8687 || lv == class_1802.field_8477 || lv == class_1802.field_8695 || lv == class_1802.field_8620;
		}

		@Override
		public int method_7675() {
			return 1;
		}
	}
}
